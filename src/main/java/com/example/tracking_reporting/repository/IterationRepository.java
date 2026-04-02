package com.example.tracking_reporting.repository;

import com.example.tracking_reporting.entity.Iteration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IterationRepository extends JpaRepository<Iteration, UUID> {
    List<Iteration> findByProjectId(UUID projectId);
    void deleteByProjectId(UUID projectId);
}
