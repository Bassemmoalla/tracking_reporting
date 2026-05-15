package com.example.tracking_reporting.dto;

import com.example.tracking_reporting.enums.TaskStatus;

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
        TaskStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}