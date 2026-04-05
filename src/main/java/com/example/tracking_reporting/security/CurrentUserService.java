package com.example.tracking_reporting.security;

import com.example.tracking_reporting.entity.User;
import com.example.tracking_reporting.exception.ResourceNotFoundException;
import com.example.tracking_reporting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new ResourceNotFoundException("Authenticated user not found");
        }

        String keycloakId = jwt.getClaimAsString("sub");
        String email = jwt.getClaimAsString("email");

        if (keycloakId != null) {
            return userRepository.findByKeycloakId(keycloakId)
                    .orElseGet(() -> userRepository.findByEmail(email)
                            .orElseThrow(() -> new ResourceNotFoundException("Local user not found for authenticated account")));
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Local user not found for authenticated account"));
    }
}