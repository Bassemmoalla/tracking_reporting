package com.example.tracking_reporting.dto;

import com.example.tracking_reporting.enums.DocumentFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReportResponse(
        UUID id,
        DocumentFormat format,
        LocalDateTime generatedAt,
        String filePath,
        LocalDateTime validatedAt,
        UUID projectId,
        String projectName
) {}