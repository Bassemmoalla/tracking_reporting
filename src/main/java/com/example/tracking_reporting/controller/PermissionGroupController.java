package com.example.tracking_reporting.controller;

import com.example.tracking_reporting.dto.CreatePermissionGroupRequest;
import com.example.tracking_reporting.dto.PermissionGroupResponse;
import com.example.tracking_reporting.dto.UpdatePermissionGroupRequest;
import com.example.tracking_reporting.service.PermissionGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/permission-groups")
@RequiredArgsConstructor
public class PermissionGroupController {

    private final PermissionGroupService permissionGroupService;

    @GetMapping
    @PreAuthorize("hasAuthority('role:view')")
    public List<PermissionGroupResponse> getAll() {
        return permissionGroupService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('role:view')")
    public PermissionGroupResponse getById(@PathVariable UUID id) {
        return permissionGroupService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('role:create')")
    public PermissionGroupResponse create(@Valid @RequestBody CreatePermissionGroupRequest request) {
        return permissionGroupService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role:update')")
    public PermissionGroupResponse update(@PathVariable UUID id, @Valid @RequestBody UpdatePermissionGroupRequest request) {
        return permissionGroupService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('role:delete')")
    public void delete(@PathVariable UUID id) {
        permissionGroupService.delete(id);
    }
}