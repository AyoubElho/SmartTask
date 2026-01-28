package com.example.smarttask_backend.user;

import com.example.smarttask_backend.task.Task;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    // For now: plain text (hash later if needed)
    @Column(nullable = false)
    private String password;
    @Column(updatable = false)

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // User → Tasks (one user owns many tasks)
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Task> tasks;

    @JsonIgnore
    @ManyToMany(mappedBy = "sharedWith")
    private Set<Task> sharedTasks;

    public User() {
        this.createdAt = LocalDateTime.now();
    }


}
