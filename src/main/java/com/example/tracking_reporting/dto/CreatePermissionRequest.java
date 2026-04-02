package com.example.tracking_reporting.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePermissionRequest(
        @NotBlank String permissionKey,
        @NotBlank String name,
        String description,
        String module
) {
}