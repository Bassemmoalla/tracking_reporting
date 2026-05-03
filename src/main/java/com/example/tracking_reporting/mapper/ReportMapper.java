package com.example.tracking_reporting.mapper;

import com.example.tracking_reporting.dto.report.GeneratedReportResponse;
import com.example.tracking_reporting.entity.Report;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

    public GeneratedReportResponse toResponse(Report report) {
        return new GeneratedReportResponse(
                report.getId(),
                report.getTitle(),
                report.getScope(),
                report.getDocumentFormat(),
                report.getReportStatus(),
                report.getProject() != null ? report.getProject().getId() : null,
                report.getIteration() != null ? report.getIteration().getId() : null,
                report.getPeriodLabel(),
                report.getS3Key(),
                report.getFileUrl(),
                report.getContentType(),
                report.getFileSize(),
                report.getCreatedBy(),
                report.getCreatedAt(),
                report.getUpdatedAt()
        );
    }
}