package com.example.tracking_reporting.dto;

import java.util.List;
import java.util.Set;

public record AuthMeResponse(
        String username,
        String email,
        String name,
        String status,
        List<String> authorities,
        Set<String> permissionKeys,
        Set<String> permissionGroupNames,
        Set<String> roleLabels
) {
}