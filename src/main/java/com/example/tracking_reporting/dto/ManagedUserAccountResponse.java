package com.example.tracking_reporting.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record ManagedUserAccountResponse(
        UUID id,
        String keycloakId,
        String firstName,
        String lastName,
        String phoneNumber,
        String email,
        String cin,
        String status,
        Set<String> permissionKeys,
        Set<String> permissionGroupNames,
        Set<String> roleLabels,
        Instant createdAt
) {
}