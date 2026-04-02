package com.example.tracking_reporting.dto;

import com.example.tracking_reporting.enums.IterationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record IterationResponse(
        UUID id,
        String name,
        BigDecimal plannedBudget,
        BigDecimal realBudget,
        LocalDate estimatedFinishDate,
        LocalDate realFinishDate,
        String objective,
        IterationStatus status,
        UUID projectId,
        String projectName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}