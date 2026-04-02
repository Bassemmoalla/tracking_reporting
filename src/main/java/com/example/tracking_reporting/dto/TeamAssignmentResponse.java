package com.example.tracking_reporting.dto;

import java.time.Instant;
import java.util.UUID;

public record TeamAssignmentResponse(
        UUID id,
        UUID teamId,
        UUID userId,
        Instant assignedAt,
        UUID assignedByUserId
) {
}