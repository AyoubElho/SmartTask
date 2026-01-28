package com.example.smarttask_backend.task;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/all") // <--- This adds "/all", making the total: "/api/all"
    public List<Task> findAll() {
        return taskService.getTasks();
    }

    @GetMapping("user/id/{userId}")
    public List<Task> getUserTasks(@PathVariable Long userId) {
        return taskService.getTasksByUser(userId);
    }

    @PostMapping("/{taskId}/share/{userId}")
    public ResponseEntity<String> shareTask(@PathVariable Long taskId, @PathVariable Long userId) {
        taskService.shareTask(taskId, userId);
        return ResponseEntity.ok("Task shared successfully!");
    }

    @PostMapping("/save") // <--- This adds "/all", making the total: "/api/all"
    public Task save(@RequestBody Task task) {
        return taskService.save(task);
    }

    @PostMapping("/create/prompt/{prompt}/userId/{userId}")
    public Task CreateTaskWithAi(@PathVariable String prompt, @PathVariable Long userId) {
        return taskService.createTaskWithAi(prompt, userId);
    }

    @PutMapping("/{taskId}/due-date")
    public void updateDueDate(
            @PathVariable Long taskId,
            @RequestBody UpdateDueDateRequest request
    ) {
        taskService.updateDueDate(taskId, request.getDueDate());
    }


    @PostMapping("/create-task/id/{userId}")
    public Task createTask(@RequestBody Task task, @PathVariable Long userId) {
        return taskService.createTask(task, userId);
    }

    @GetMapping("/test")
    public String test() {
        return "OK";
    }

    @GetMapping("/{id}")
    public Task findById(@PathVariable Long id) {
        return taskService.findById(id);
    }


    @PutMapping("/{taskId}/status/{status}")
    public void updateStatus(@PathVariable Long taskId,@PathVariable Status status) {
        taskService.updateStatus(taskId, status);
    }


    @GetMapping("/shared/{userId}")
    public List<Task> getSharedTasks(@PathVariable Long userId) {
        return taskService.getTasksSharedWithUser(userId);
    }

}