package com.example.tracking_reporting.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePermissionRequest(
        @NotBlank String permissionKey,
        @NotBlank String name,
        String description,
        String module
) {}
