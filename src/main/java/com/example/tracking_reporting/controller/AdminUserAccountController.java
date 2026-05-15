package com.example.tracking_reporting.controller;

import com.example.tracking_reporting.dto.CreateManagedUserAccountRequest;
import com.example.tracking_reporting.dto.ManagedUserAccountResponse;
import com.example.tracking_reporting.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserAccountController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('user:create')")
    public ManagedUserAccountResponse createManagedAccount(@Valid @RequestBody CreateManagedUserAccountRequest request) {
        return userService.createManagedAccount(request);
    }
}
