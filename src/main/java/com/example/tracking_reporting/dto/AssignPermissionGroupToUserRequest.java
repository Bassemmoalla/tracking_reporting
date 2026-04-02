package com.example.tracking_reporting.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignPermissionGroupToUserRequest(
        @NotNull UUID userId,
        @NotNull UUID permissionGroupId
) {
}