package com.example.tracking_reporting.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        String name,
        String shortText,
        LocalDate endTaskDate,
        String description,
        UUID projectId,
        String projectName,
        UUID assignedUserId,
        String assignedUserName,
        UUID iterationId,
        String iterationName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}