package com.example.tracking_reporting.service.report;

import com.example.tracking_reporting.dto.AuditLogResponse;
import com.example.tracking_reporting.entity.AuditLog;
import com.example.tracking_reporting.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAll() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::map)
                .toList();
    }

    private AuditLogResponse map(AuditLog log) {
        return new AuditLogResponse(
                log.getId(),
                log.getAction(),
                log.getActor(),
                log.getResourceType(),
                log.getResourceId(),
                log.getDetails(),
                log.getCreatedAt()
        );
    }
}