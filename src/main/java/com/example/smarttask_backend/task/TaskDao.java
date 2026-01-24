package com.example.smarttask_backend.task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskDao extends JpaRepository<Task, Long> {

    List<Task> findByUserId(Long userId);


}
