package com.example.tracking_reporting.repository;

import com.example.tracking_reporting.entity.Task;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByProjectId(UUID projectId);

    void deleteByProjectId(UUID projectId);

    @Modifying
    @Transactional
    @Query("update Task t set t.iteration = null where t.iteration.id = :iterationId")
    void clearIterationFromTasks(@Param("iterationId") UUID iterationId);
}
