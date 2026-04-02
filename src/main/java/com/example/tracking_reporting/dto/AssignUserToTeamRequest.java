package com.example.tracking_reporting.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignUserToTeamRequest(
        @NotNull UUID teamId,
        @NotNull UUID userId,
        UUID assignedByUserId
) {
}