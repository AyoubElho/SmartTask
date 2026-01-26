package com.example.smarttask_backend.subTask;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubTaskDao extends JpaRepository<SubTask, Long> {

    // ✅ Get all subtasks of a task
    List<SubTask> findByTaskId(Long taskId);
}
