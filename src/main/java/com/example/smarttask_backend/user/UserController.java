package com.example.smarttask_backend.user;


import com.example.smarttask_backend.task.Task;
import com.example.smarttask_backend.task.TaskDao;
import com.example.smarttask_backend.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.register(user);
    }

    @GetMapping("/")
    public List<User> findAllUsers() {
        return userService.findAllUsers();
    }



    @PostMapping("/login")
    public User login(@RequestBody LoginRequest request) {
        return userService.login(
                request.getEmail(),
                request.getPassword()
        );
    }


}
