package com.example.tracking_reporting.dto;

import java.time.Instant;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        String title,
        String content,
        UUID projectId,
        String projectName,
        UUID taskId,
        String taskName,
        String createdBy,
        Instant createdAt,
        Instant updatedAt
) {
}