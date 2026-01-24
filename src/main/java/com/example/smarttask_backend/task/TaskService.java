package com.example.smarttask_backend.task;

import com.example.smarttask_backend.ai.AIService;
import com.example.smarttask_backend.subTask.SubTask;
import com.example.smarttask_backend.user.User;
import com.example.smarttask_backend.user.UserDao;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TaskService {
    @Autowired
    private TaskDao taskDao;

    @Autowired
    private UserDao userDao;
    @Autowired
    private AIService aiService;

    public List<Task> getTasks() {
        return taskDao.findAll();
    }

    public Task save(Task task) {
        task.setDueDate(task.getDueDate());
        return taskDao.save(task);
    }


    public Task createTask(Task task, Long userId) {
        Optional<User> user = userDao.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        task.setUser(user.get());
        return taskDao.save(task);
    }

    public List<Task> getTasksByUser(Long userId) {
        return taskDao.findByUserId(userId);
    }

    @Transactional
    public void updateDueDate(Long taskId, LocalDateTime newDueDate) {

        Task task = taskDao.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (task.getStatus() == null) {
            task.setStatus(Status.TODO);
        }

        task.setDueDate(newDueDate);
    }


    public Task findById(Long id) {
        return taskDao.findById(id).get();
    }

    public Task createTaskWithAi(String prompt, Long userId) {

        // 1️⃣ Parse task using AI
        Task task = aiService.parseTextToTask(prompt);
        if (task == null) {
            throw new RuntimeException("Invalid AI response");
        }

        // 2️⃣ Load user
        User user = userDao.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // 3️⃣ Attach user (CRITICAL)
        task.setUser(user);

        // 4️⃣ Backend-controlled defaults
        task.setStatus(Status.TODO);

        if (task.getSubTasks() == null) {
            task.setSubTasks(new HashSet<>());
        }

        // 5️⃣ Save
        return taskDao.save(task);
    }

}
