package com.example.tracking_reporting.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePermissionGroupRequest(
        @NotBlank String name,
        String description
) {
}