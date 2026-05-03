package com.example.tracking_reporting.service;

import com.example.tracking_reporting.dto.report.GenerateReportRequest;
import com.example.tracking_reporting.dto.report.GeneratedReportResponse;
import com.example.tracking_reporting.dto.report.ReportSearchItem;
import com.example.tracking_reporting.dto.report.UpdateGeneratedReportRequest;

import java.util.List;
import java.util.UUID;

public interface ReportService {
    GeneratedReportResponse generate(GenerateReportRequest request);
    GeneratedReportResponse getById(UUID id);
    List<GeneratedReportResponse> getAll();
    List<ReportSearchItem> search(String query);
    GeneratedReportResponse update(UUID id, UpdateGeneratedReportRequest request);
    void delete(UUID id);
}