
package com.example.tracking_reporting.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CommentRequest(
        @NotBlank String title,
        @NotBlank String content,
        UUID projectId,
        UUID taskId
) {
}