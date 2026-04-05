package com.example.tracking_reporting.controller;

import com.example.tracking_reporting.dto.CreatePermissionRequest;
import com.example.tracking_reporting.dto.PermissionResponse;
import com.example.tracking_reporting.dto.UpdatePermissionRequest;
import com.example.tracking_reporting.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasAuthority('permission:view')")
    public List<PermissionResponse> getAll() {
        return permissionService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:view')")
    public PermissionResponse getById(@PathVariable UUID id) {
        return permissionService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('permission:create')")
    public PermissionResponse create(@Valid @RequestBody CreatePermissionRequest request) {
        return permissionService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:update')")
    public PermissionResponse update(@PathVariable UUID id, @Valid @RequestBody UpdatePermissionRequest request) {
        return permissionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('permission:delete')")
    public void delete(@PathVariable UUID id) {
        permissionService.delete(id);
    }
}