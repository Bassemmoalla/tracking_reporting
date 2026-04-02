package com.example.tracking_reporting.dto;

import com.example.tracking_reporting.enums.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectRequest(
        @NotBlank String name,
        String description,
        ProjectStatus status,
        LocalDateTime deadline,
        @NotNull UUID teamId
) {}