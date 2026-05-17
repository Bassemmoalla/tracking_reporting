package com.example.tracking_reporting.dto;

import java.time.Instant;
import java.util.UUID;

public record TeamResponse(
        UUID id,
        String name,
        String description,
        boolean archived,
        Instant createdAt,
        Instant updatedAt
) {
}