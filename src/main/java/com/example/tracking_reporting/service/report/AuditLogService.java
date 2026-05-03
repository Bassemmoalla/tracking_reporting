package com.example.tracking_reporting.service.report;

import com.example.tracking_reporting.entity.AuditLog;
import com.example.tracking_reporting.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void save(String action, String actor, String resourceType, String resourceId, String details) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setActor(actor);
        log.setResourceType(resourceType);
        log.setResourceId(resourceId);
        log.setDetails(details);
        auditLogRepository.save(log);
    }
}
