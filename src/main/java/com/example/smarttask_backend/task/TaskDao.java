package com.example.smarttask_backend.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskDao extends JpaRepository<Task, Long> {

    List<Task> findByUserId(Long userId);

    @Query("""
        SELECT t FROM Task t
        JOIN t.sharedWith u
        WHERE u.id = :userId
    """)
    List<Task> findTasksSharedWithUser(@Param("userId") Long userId);
}
