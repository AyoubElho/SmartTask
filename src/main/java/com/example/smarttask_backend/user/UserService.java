package com.example.smarttask_backend.user;

import com.example.smarttask_backend.task.Task;
import com.example.smarttask_backend.task.TaskDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User findById(Long id) {
        return userDao.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }


    public List<User> findAllUsers() {
        return userDao.findAll();
    }

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userDao.save(user);
    }

    public User login(String email, String rawPassword) {

        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());

        if (!matches) {
            throw new RuntimeException("Invalid username or password");
        }

        return user; // password is hidden with @JsonIgnore
    }

}
