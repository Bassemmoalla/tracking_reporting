package com.example.tracking_reporting.controller;

import com.example.tracking_reporting.dto.*;
import com.example.tracking_reporting.service.PermissionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping
    public PermissionResponse create(@Valid @RequestBody CreatePermissionRequest request) {
        return permissionService.create(request);
    }

    @GetMapping
    public List<PermissionResponse> getAll() {
        return permissionService.getAll();
    }

    @GetMapping("/{id}")
    public PermissionResponse getById(@PathVariable UUID id) {
        return permissionService.getById(id);
    }

    @PutMapping("/{id}")
    public PermissionResponse update(@PathVariable UUID id,
                                     @Valid @RequestBody UpdatePermissionRequest request) {
        return permissionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        permissionService.delete(id);
    }
}