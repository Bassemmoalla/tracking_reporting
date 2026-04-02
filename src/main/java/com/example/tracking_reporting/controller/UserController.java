package com.example.tracking_reporting.controller;

import com.example.tracking_reporting.dto.*;
import com.example.tracking_reporting.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) { this.userService = userService; }

    @PostMapping
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return userService.create(request);
    }

    @PostMapping("/assign-permission-group")
    public UserResponse assignPermissionGroup(@Valid @RequestBody AssignPermissionGroupToUserRequest request) {
        return userService.assignPermissionGroup(request);
    }

    @GetMapping
    public List<UserResponse> getAll() { return userService.getAll(); }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable UUID id) { return userService.getById(id); }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable UUID id,
                               @Valid @RequestBody UpdateUserRequest request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }
}