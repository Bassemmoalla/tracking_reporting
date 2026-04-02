package com.example.tracking_reporting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record TaskRequest(
        @NotBlank String name,
        String shortText,
        LocalDate endTaskDate,
        String description,
        @NotNull UUID projectId,
        UUID assignedUserId,
        UUID iterationId
) {}