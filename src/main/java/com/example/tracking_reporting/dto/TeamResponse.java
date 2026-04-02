package com.example.tracking_reporting.dto;

import java.util.UUID;

public record TeamResponse(
        UUID id,
        String name,
        String description
) {
}