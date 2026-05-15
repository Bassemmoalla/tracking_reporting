package com.example.tracking_reporting.controller;

import com.example.tracking_reporting.dto.AuthMeResponse;
import com.example.tracking_reporting.entity.User;
import com.example.tracking_reporting.exception.ResourceNotFoundException;
import com.example.tracking_reporting.security.CurrentUserService;
import com.example.tracking_reporting.service.UserAccessProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CurrentUserService currentUserService;
    private final UserAccessProfileService userAccessProfileService;

    @GetMapping("/me")
    public AuthMeResponse me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found");
        }

        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(Object::toString)
                .distinct()
                .sorted()
                .toList();

        User localUser = null;
        try {
            localUser = currentUserService.getCurrentUser();
        } catch (ResourceNotFoundException ignored) {
        }

        Set<String> permissionKeys = localUser != null
                ? userAccessProfileService.extractPermissionKeys(localUser)
                : new LinkedHashSet<>(authorities);

        Set<String> permissionGroupNames = localUser != null
                ? userAccessProfileService.extractPermissionGroupNames(localUser)
                : new LinkedHashSet<>();

        Set<String> roleLabels = localUser != null
                ? userAccessProfileService.resolveRoleLabels(localUser)
                : userAccessProfileService.resolveRoleLabels(permissionKeys, permissionGroupNames);

        return new AuthMeResponse(
                jwt.getClaimAsString("preferred_username"),
                jwt.getClaimAsString("email"),
                jwt.getClaimAsString("name"),
                localUser != null && localUser.getStatus() != null ? localUser.getStatus().name() : null,
                authorities,
                permissionKeys,
                permissionGroupNames,
                roleLabels
        );
    }
}