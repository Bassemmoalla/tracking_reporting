package com.example.tracking_reporting.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignPermissionToGroupRequest(
        @NotNull UUID permissionGroupId,
        @NotNull UUID permissionId
) {
}