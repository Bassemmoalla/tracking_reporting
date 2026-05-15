package com.example.tracking_reporting.dto;

import java.time.Instant;
import java.util.UUID;

public record AuditLogResponse(
        UUID id,
        String action,
        String username,
        String entityType,
        String entityId,
        String details,
        Instant createdAt
) {
}
