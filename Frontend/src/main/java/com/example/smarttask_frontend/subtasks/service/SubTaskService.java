package com.example.smarttask_frontend.subtasks.service;

import com.example.smarttask_frontend.dto.StatusRequest;
import com.example.smarttask_frontend.entity.SubTask;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class SubTaskService {

    private static final String BASE_URL = "http://localhost:8080/subtasks";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // =========================
    // GET SUBTASKS BY TASK ID
    // =========================
    public List<SubTask> getSubTasksByTaskId(Long taskId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/task/" + taskId))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return objectMapper.readValue(
                    response.body(),
                    new TypeReference<List<SubTask>>() {}
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch subtasks", e);
        }
    }

    // =========================
    // UPDATE SUBTASK STATUS
    // =========================
    public void updateSubTaskStatus(Long subTaskId, boolean completed) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            BASE_URL + "/" + subTaskId + "/status?is_completed=" + completed
                    ))
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Update failed: " + response.statusCode());
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to update subtask status", e);
        }
    }

    public SubTask addSubTask(Long taskId, SubTask subTask) {
        try {
            String json = objectMapper.writeValueAsString(subTask);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/add/" + taskId))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200 && response.statusCode() != 201) {
                throw new RuntimeException("Failed to add subtask");
            }

            return objectMapper.readValue(response.body(), SubTask.class);

        } catch (Exception e) {
            throw new RuntimeException("Error adding subtask", e);
        }
    }

}
