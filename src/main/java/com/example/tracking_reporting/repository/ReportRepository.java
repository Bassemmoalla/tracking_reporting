package com.example.tracking_reporting.repository;

import com.example.tracking_reporting.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    List<Report> findByProjectId(UUID projectId);
    void deleteByProjectId(UUID projectId);
}