package com.example.smarttask_backend.subTask;

import com.example.smarttask_backend.task.Task;
import com.example.smarttask_backend.task.TaskDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
