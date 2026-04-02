package com.example.tracking_reporting.service;

import com.example.tracking_reporting.dto.ReportRequest;
import com.example.tracking_reporting.dto.ReportResponse;

import java.util.List;
import java.util.UUID;

public interface ReportService {
    List<ReportResponse> getAll();
    ReportResponse getById(UUID id);
    List<ReportResponse> getByProjectId(UUID projectId);
    ReportResponse create(ReportRequest request);
    ReportResponse update(UUID id, ReportRequest request);
    void delete(UUID id);
}