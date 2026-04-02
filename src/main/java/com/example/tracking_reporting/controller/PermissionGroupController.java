package com.example.tracking_reporting.controller;

import com.example.tracking_reporting.dto.*;
import com.example.tracking_reporting.service.PermissionGroupService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/permission-groups")
public class PermissionGroupController {

    private final PermissionGroupService permissionGroupService;

    public PermissionGroupController(PermissionGroupService permissionGroupService) {
        this.permissionGroupService = permissionGroupService;
    }

    @PostMapping
    public PermissionGroupResponse create(@Valid @RequestBody CreatePermissionGroupRequest request) {
        return permissionGroupService.create(request);
    }

    @PostMapping("/assign-permission")
    public PermissionGroupResponse assignPermission(@Valid @RequestBody AssignPermissionToGroupRequest request) {
        return permissionGroupService.assignPermission(request);
    }

    @GetMapping
    public List<PermissionGroupResponse> getAll() {
        return permissionGroupService.getAll();
    }

    @GetMapping("/{id}")
    public PermissionGroupResponse getById(@PathVariable UUID id) {
        return permissionGroupService.getById(id);
    }

    @PutMapping("/{id}")
    public PermissionGroupResponse update(@PathVariable UUID id,
                                          @Valid @RequestBody UpdatePermissionGroupRequest request) {
        return permissionGroupService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        permissionGroupService.delete(id);
    }
}