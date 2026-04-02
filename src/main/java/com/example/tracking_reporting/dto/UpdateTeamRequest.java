package com.example.tracking_reporting.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateTeamRequest(
        @NotBlank String name,
        String description
) {}