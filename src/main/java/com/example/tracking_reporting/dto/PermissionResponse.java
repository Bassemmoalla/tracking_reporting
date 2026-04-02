package com.example.tracking_reporting.dto;

import java.util.UUID;

public record PermissionResponse(
        UUID id,
        String permissionKey,
        String name,
        String description,
        String module
) {
}