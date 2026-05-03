package com.example.tracking_reporting.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.gitlab")
public record GitLabProperties(
        String baseUrl,
        String token
) {
}