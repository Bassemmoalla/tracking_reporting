package com.example.tracking_reporting.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String phoneNumber,
        @Email @NotBlank String email,
        @NotBlank String cin,
        @NotBlank String status
) {}