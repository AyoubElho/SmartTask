package com.example.smarttask_backend.subTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subtasks")
public class SubController {


    @Autowired
    private SubTaskService subTaskService;

    @PostMapping("/add/{taskId}")
    public SubTask addSubTask(@PathVariable Long taskId, @RequestBody SubTask subTask) {
        return subTaskService.addSubTask(taskId, subTask);
    }

}
