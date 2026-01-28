package com.example.smarttask_frontend.tasks.service;

import com.example.smarttask_frontend.AppConfig;
import com.example.smarttask_frontend.dto.UpdateDueDateRequest;
import com.example.smarttask_frontend.entity.Task;
import com.example.smarttask_frontend.entity.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

public class TaskService {

    private static final String BASE_URL = AppConfig.get("backend.base-url");

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public List<User> getUsers() {
        try {
            String url = BASE_URL + "user";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(
                        response.body(),
                        new TypeReference<List<User>>() {
                        });
            } else {
                throw new RuntimeException("Failed to load users. Status: " + response.statusCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public boolean shareTaskWithUser(Long taskId, Long userId) {
        try {
            String url = BASE_URL + taskId + "/share/" + userId;
            System.out.println(url);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
    
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
    
            return response.statusCode() == 200 || response.statusCode() == 204;
    
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Task> getTasksByUser(Long userId) throws Exception {

        String url = BASE_URL + "user/id/" + userId;
        System.out.println("Calling: " + url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(
                    response.body(),
                    new TypeReference<List<Task>>() {
                    });
        } else {
            System.out.println("STATUS: " + response.statusCode());
            System.out.println("BODY: " + response.body());
            throw new RuntimeException("Failed to load tasks, status: " + response.statusCode());
        }
    }

    public Task createTask(Task task, Long userId) {

        try {
            String url = BASE_URL + "create-task/id/" + userId;

            String json = objectMapper.writeValueAsString(task);
            System.out.println(json);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return objectMapper.readValue(response.body(), Task.class);
            }

            throw new RuntimeException(
                    "Failed to create task. Status: " + response.statusCode());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateDueDate(Long taskId, LocalDateTime newDueDate) {

        try {
            String url = BASE_URL + taskId + "/due-date";

            UpdateDueDateRequest body = new UpdateDueDateRequest(newDueDate);

            String json = objectMapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());

            if (response.statusCode() != 200 && response.statusCode() != 204) {
                throw new RuntimeException("Failed to update due date");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateTaskStatus(Long taskId, String status) {
        try {
            String url = BASE_URL + taskId + "/status/" + status;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());

            if (response.statusCode() != 200 && response.statusCode() != 204) {
                throw new RuntimeException(
                        "Failed to update status. HTTP " + response.statusCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Task> getSharedTasks(Long userId) {
        try {
            String url = BASE_URL + "shared/" + userId;
    
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
    
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return objectMapper.readValue(
                        response.body(),
                        new TypeReference<List<Task>>() {}
                );
            }
    
            throw new RuntimeException("Failed to load shared tasks");
    
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

}
