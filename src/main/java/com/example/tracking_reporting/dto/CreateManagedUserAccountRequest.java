package com.example.tracking_reporting.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CreateManagedUserAccountRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String phoneNumber,
        @Email @NotBlank String email,
        @NotBlank String cin,
        @NotBlank String status,
        @NotBlank @Size(min = 8)String password,
        List<String> permissionKeys,
        List<UUID> permissionGroupIds
) {
}