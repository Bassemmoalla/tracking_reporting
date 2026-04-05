package com.example.tracking_reporting.controller;

import com.example.tracking_reporting.dto.ReportRequest;
import com.example.tracking_reporting.dto.ReportResponse;
import com.example.tracking_reporting.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    @PreAuthorize("hasAuthority('report:view')")
    public List<ReportResponse> getAll() {
        return reportService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('report:view')")
    public ReportResponse getById(@PathVariable UUID id) {
        return reportService.getById(id);
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAuthority('report:view')")
    public List<ReportResponse> getByProjectId(@PathVariable UUID projectId) {
        return reportService.getByProjectId(projectId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('report:create')")
    public ReportResponse create(@Valid @RequestBody ReportRequest request) {
        return reportService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('report:update')")
    public ReportResponse update(@PathVariable UUID id, @Valid @RequestBody ReportRequest request) {
        return reportService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('report:delete')")
    public void delete(@PathVariable UUID id) {
        reportService.delete(id);
    }
}
