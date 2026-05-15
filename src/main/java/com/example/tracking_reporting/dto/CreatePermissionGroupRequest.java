package com.example.tracking_reporting.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record CreatePermissionGroupRequest(
        @NotBlank String name,
        String description,
        Set<String> permissionKeys
) {
}