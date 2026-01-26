package com.example.smarttask_backend.subTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subtasks")
public class SubController {

    @Autowired
    private SubTaskService subTaskService;

    // ✅ ADD SUBTASK
    @PostMapping("/add/{taskId}")
    public SubTask addSubTask(@PathVariable Long taskId,
                              @RequestBody SubTask subTask) {
        return subTaskService.addSubTask(taskId, subTask);
    }
    @PutMapping("/{id}/status")
    public SubTask updateSubTaskStatus(
            @PathVariable Long id,
            @RequestParam("is_completed") boolean completed) {
        return subTaskService.markSubTaskAsDone(id, completed);
    }
    // ✅ GET SUBTASK LIST BY TASK ID
    @GetMapping("/task/{taskId}")
    public List<SubTask> getSubTasksByTaskId(@PathVariable Long taskId) {
        return subTaskService.getSubTasksByTaskId(taskId);
    }
}
