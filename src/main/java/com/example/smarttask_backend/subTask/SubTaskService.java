package com.example.smarttask_backend.subTask;

import com.example.smarttask_backend.task.Task;
import com.example.smarttask_backend.task.TaskDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SubTaskService {

    @Autowired
    private SubTaskDao subTaskDao;

    @Autowired
    private TaskDao taskDao;

    public SubTask addSubTask(Long taskId, SubTask subTask) {
        Task task = taskDao.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        subTask.setTask(task);
        return subTaskDao.save(subTask);
    }
    public SubTask markSubTaskAsDone(Long subTaskId, boolean completed) {
        SubTask subTask = subTaskDao.findById(subTaskId)
                .orElseThrow(() -> new RuntimeException("SubTask not found"));

        subTask.setCompleted(completed);
        return subTaskDao.save(subTask);
    }

    // ✅ GET SUBTASK LIST BY TASK ID
    public List<SubTask> getSubTasksByTaskId(Long taskId) {
        return subTaskDao.findByTaskId(taskId);
    }

    // ✅ OPTIONAL: GET SUBTASK BY ID
    public SubTask getSubTaskById(Long subTaskId) {
        return subTaskDao.findById(subTaskId)
                .orElseThrow(() -> new RuntimeException("SubTask not found"));
    }
}
