package com.example.tracking_reporting.repository;

import com.example.tracking_reporting.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {

    List<Report> findByProject_Id(UUID projectId);

    List<Report> findByIteration_Id(UUID iterationId);

    @Modifying
    @Transactional
    void deleteByProject_Id(UUID projectId);

    default void deleteByProjectId(UUID projectId) {
        deleteByProject_Id(projectId);
    }
}