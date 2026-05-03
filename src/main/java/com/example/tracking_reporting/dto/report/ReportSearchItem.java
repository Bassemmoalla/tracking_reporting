package com.example.tracking_reporting.dto.report;

import com.example.tracking_reporting.enums.DocumentFormat;
import com.example.tracking_reporting.enums.ReportScope;

import java.time.Instant;
import java.util.UUID;

public record ReportSearchItem(
        UUID id,
        String title,
        ReportScope scope,
        DocumentFormat format,
        UUID projectId,
        UUID iterationId,
        String periodLabel,
        String fileUrl,
        String createdBy,
        Instant createdAt
) {
}
