package com.example.tracking_reporting.dto.report;

import com.example.tracking_reporting.enums.DocumentFormat;
import com.example.tracking_reporting.enums.ReportScope;
import com.example.tracking_reporting.enums.ReportStatus;

import java.time.Instant;
import java.util.UUID;

public record GeneratedReportResponse(
        UUID id,
        String title,
        ReportScope scope,
        DocumentFormat format,
        ReportStatus status,
        UUID projectId,
        UUID iterationId,
        String periodLabel,
        String s3Key,
        String fileUrl,
        String contentType,
        long fileSize,
        String createdBy,
        Instant createdAt,
        Instant updatedAt
) {
}
