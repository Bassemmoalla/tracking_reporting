package com.example.tracking_reporting.dto.report;

import com.example.tracking_reporting.enums.DocumentFormat;
import com.example.tracking_reporting.enums.ReportScope;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GenerateReportRequest(
        @NotBlank String title,
        @NotNull ReportScope scope,
        @NotNull DocumentFormat format,
        @NotBlank String periodLabel,
        UUID projectId,
        UUID iterationId
) {
}