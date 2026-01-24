package com.example.smarttask_backend.ai;

import com.example.smarttask_backend.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/parse")
    public Task parse(@RequestBody String text) {
        return aiService.parseTextToTask(text);
    }
}
