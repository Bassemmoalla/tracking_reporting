package com.example.tracking_reporting.dto;

import com.example.tracking_reporting.enums.DocumentFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReportRequest(
        @NotNull DocumentFormat format,
        LocalDateTime generatedAt,
        String filePath,
        LocalDateTime validatedAt,
        @NotNull UUID projectId
) {}