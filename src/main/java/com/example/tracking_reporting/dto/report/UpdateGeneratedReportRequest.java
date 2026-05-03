package com.example.tracking_reporting.dto.report;

import jakarta.validation.constraints.NotBlank;

public record UpdateGeneratedReportRequest(
        @NotBlank String title,
        @NotBlank String periodLabel
) {
}