package com.example.tracking_reporting.controller;

import com.example.tracking_reporting.dto.TaskRequest;
import com.example.tracking_reporting.dto.TaskResponse;
import com.example.tracking_reporting.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    @PreAuthorize("hasAuthority('task:view')")
    public List<TaskResponse> getAll() {
        return taskService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('task:view')")
    public TaskResponse getById(@PathVariable UUID id) {
        return taskService.getById(id);
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAuthority('task:view')")
    public List<TaskResponse> getByProjectId(@PathVariable UUID projectId) {
        return taskService.getByProjectId(projectId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('task:create')")
    public TaskResponse create(@Valid @RequestBody TaskRequest request) {
        return taskService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('task:update')")
    public TaskResponse update(@PathVariable UUID id, @Valid @RequestBody TaskRequest request) {
        return taskService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('task:delete')")
    public void delete(@PathVariable UUID id) {
        taskService.delete(id);
    }
}