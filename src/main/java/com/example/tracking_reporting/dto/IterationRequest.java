package com.example.tracking_reporting.dto;

import com.example.tracking_reporting.enums.IterationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record IterationRequest(
        @NotBlank String name,
        BigDecimal plannedBudget,
        BigDecimal realBudget,
        LocalDate estimatedFinishDate,
        LocalDate realFinishDate,
        String objective,
        IterationStatus status,
        @NotNull UUID projectId
) {}