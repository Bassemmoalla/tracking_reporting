package com.example.tracking_reporting.dto;

import java.util.Set;
import java.util.UUID;

public record PermissionGroupResponse(
        UUID id,
        String name,
        String description,
        Set<String> permissions
) {
}
