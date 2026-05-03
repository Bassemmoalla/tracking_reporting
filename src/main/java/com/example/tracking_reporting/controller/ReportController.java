package com.example.tracking_reporting.controller;

import com.example.tracking_reporting.dto.report.GenerateReportRequest;
import com.example.tracking_reporting.dto.report.GeneratedReportResponse;
import com.example.tracking_reporting.dto.report.ReportSearchItem;
import com.example.tracking_reporting.dto.report.UpdateGeneratedReportRequest;
import com.example.tracking_reporting.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/generate")
    @PreAuthorize("hasAuthority('report:generate')")
    public GeneratedReportResponse generate(@Valid @RequestBody GenerateReportRequest request) {
        return reportService.generate(request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('report:view')")
    public List<GeneratedReportResponse> getAll() {
        return reportService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('report:view')")
    public GeneratedReportResponse getById(@PathVariable UUID id) {
        return reportService.getById(id);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('report:view')")
    public List<ReportSearchItem> search(@RequestParam String q) {
        return reportService.search(q);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('report:validate')")
    public GeneratedReportResponse update(@PathVariable UUID id,
                                          @Valid @RequestBody UpdateGeneratedReportRequest request) {
        return reportService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('report:validate')")
    public void delete(@PathVariable UUID id) {
        reportService.delete(id);
    }
}