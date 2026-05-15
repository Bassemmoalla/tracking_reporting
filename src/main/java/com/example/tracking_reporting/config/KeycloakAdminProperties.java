package com.example.tracking_reporting.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.keycloak.admin")
public record KeycloakAdminProperties(
        String serverUrl,
        String adminRealm,
        String targetRealm,
        String clientId,
        String username,
        String password,
        String targetClientId
) {
}