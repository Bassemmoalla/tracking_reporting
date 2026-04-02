package com.example.tracking_reporting.dto;

import com.example.tracking_reporting.enums.ProjectStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        String name,
        String description,
        ProjectStatus status,
        LocalDateTime deadline,
        UUID teamId,
        String teamName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}