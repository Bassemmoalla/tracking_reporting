package com.example.tracking_reporting.controller;

import com.example.tracking_reporting.dto.AuditLogResponse;
import com.example.tracking_reporting.service.report.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('platform:configure', 'user:create', 'user:delete')")
    public List<AuditLogResponse> getAll() {
        return auditLogService.getAll();
    }
}