package com.example.smarttask_backend.ai;

import com.example.smarttask_backend.task.Priority;
import com.example.smarttask_backend.task.Status;
import com.example.smarttask_backend.task.Task;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AIService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final ObjectMapper mapper = new ObjectMapper();

    public Task parseTextToTask(String inputText) {

        try {
            // ================== 1️⃣ PROMPT ==================


            String prompt = """
                    Convert the following text into JSON.
                    Return ONLY valid JSON.
                    Do NOT use markdown.
                    Do NOT use backticks.
                    Do NOT explain anything.
                    
                    JSON format:
                    {
                      "title": "string",
                      "priority": "LOW | MEDIUM | HIGH",
                      "description": "string or null",
                      "dueDate": "yyyy-MM-ddTHH:mm"
                    }
                    
                    Rules for dueDate:
                    1. Return null if the user does not mention a date or time.
                    2. If the user mentions a time but not a date, use today's date (%s).
                    3. If the user mentions a date but not a time, use 00:00 for the time.
                    4. For relative dates (e.g., "tomorrow"), calculate based on today's date.
                    
                    Text:
                    "%s"
                    """.formatted(LocalDate.now(), inputText);

            // ================== 2️⃣ BUILD REQUEST BODY ==================
            ObjectNode bodyNode = mapper.createObjectNode();

            ArrayNode contentsArray = mapper.createArrayNode();
            ObjectNode contentObject = mapper.createObjectNode();
            ArrayNode partsArray = mapper.createArrayNode();

            ObjectNode textPart = mapper.createObjectNode();
            textPart.put("text", prompt);

            partsArray.add(textPart);
            contentObject.set("parts", partsArray);
            contentsArray.add(contentObject);

            bodyNode.set("contents", contentsArray);

            String body = mapper.writeValueAsString(bodyNode);

            // ================== 3️⃣ HTTP REQUEST ==================
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent"
                                    + "?key=" + geminiApiKey
                    ))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            // ================== 4️⃣ DEBUG RAW RESPONSE ==================
            System.out.println("========== GEMINI RAW RESPONSE ==========");
            System.out.println(response.body());
            System.out.println("========================================");

            JsonNode root = mapper.readTree(response.body());

            // ================== 5️⃣ HANDLE GEMINI ERROR ==================
            if (root.has("error")) {
                throw new RuntimeException(
                        "Gemini error: " + root.get("error").get("message").asText()
                );
            }

            JsonNode candidates = root.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new RuntimeException("No candidates returned by Gemini");
            }

            // ================== 6️⃣ EXTRACT AI TEXT ==================
            String aiText = candidates.get(0)
                    .get("content")
                    .get("parts")
                    .get(0)
                    .get("text")
                    .asText();

            System.out.println("========== AI TEXT ==========");
            System.out.println(aiText);
            System.out.println("=============================");

            // ================== 7️⃣ CLEAN + VALIDATE ==================
            String cleanedJson = cleanJson(aiText);

            if (!cleanedJson.startsWith("{")) {
                throw new RuntimeException("AI did not return valid JSON");
            }

            JsonNode node = mapper.readTree(cleanedJson);

            // ================== 8️⃣ MAP TO TASK ==================
            Task task = new Task();
            task.setTitle(node.get("title").asText());
            task.setPriority(Priority.valueOf(node.get("priority").asText()));
            task.setStatus(Status.TODO);

            if (node.has("dueDate") && !node.get("dueDate").isNull()) {
                task.setDueDate(LocalDateTime.parse(node.get("dueDate").asText()));
            }

            return task;

        } catch (Exception e) {
            throw new RuntimeException("Gemini AI parsing failed", e);
        }
    }

    // ================== 🔧 JSON CLEANER ==================
    private String cleanJson(String text) {
        text = text.trim();

        // Remove markdown fences if Gemini adds them
        if (text.startsWith("```")) {
            text = text.replaceAll("(?s)```json", "")
                    .replaceAll("```", "")
                    .trim();
        }

        return text;
    }
}
