package com.example.smarttask_backend.subTask;

import com.example.smarttask_backend.task.Task;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SubTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private boolean isCompleted;
    @JsonIgnore
    @ManyToOne
    private Task task;
}
