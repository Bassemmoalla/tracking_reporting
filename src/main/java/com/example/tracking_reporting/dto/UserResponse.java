package com.example.tracking_reporting.dto;

import java.util.Set;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String cin,
        String status,
        Set<String> permissionGroups
) {
}