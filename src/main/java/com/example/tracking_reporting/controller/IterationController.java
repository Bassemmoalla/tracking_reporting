package com.example.tracking_reporting.controller;

import com.example.tracking_reporting.dto.IterationRequest;
import com.example.tracking_reporting.dto.IterationResponse;
import com.example.tracking_reporting.service.IterationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/iterations")
@RequiredArgsConstructor
public class IterationController {

    private final IterationService iterationService;

    @GetMapping
    @PreAuthorize("hasAuthority('iteration:view')")
    public List<IterationResponse> getAll() {
        return iterationService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('iteration:view')")
    public IterationResponse getById(@PathVariable UUID id) {
        return iterationService.getById(id);
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAuthority('iteration:view')")
    public List<IterationResponse> getByProjectId(@PathVariable UUID projectId) {
        return iterationService.getByProjectId(projectId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('iteration:create')")
    public IterationResponse create(@Valid @RequestBody IterationRequest request) {
        return iterationService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('iteration:update')")
    public IterationResponse update(@PathVariable UUID id, @Valid @RequestBody IterationRequest request) {
        return iterationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('iteration:delete')")
    public void delete(@PathVariable UUID id) {
        iterationService.delete(id);
    }
}
