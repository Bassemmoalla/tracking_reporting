package com.example.tracking_reporting.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePermissionGroupRequest(
        @NotBlank String name,
        String description
) {}