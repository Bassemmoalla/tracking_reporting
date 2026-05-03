package com.example.tracking_reporting.service.impl;

import com.example.tracking_reporting.dto.report.*;
import com.example.tracking_reporting.entity.Iteration;
import com.example.tracking_reporting.entity.Project;
import com.example.tracking_reporting.entity.Report;
import com.example.tracking_reporting.enums.ReportScope;
import com.example.tracking_reporting.enums.ReportStatus;
import com.example.tracking_reporting.exception.ResourceNotFoundException;
import com.example.tracking_reporting.helper.EntityFinder;
import com.example.tracking_reporting.repository.ReportRepository;
import com.example.tracking_reporting.security.CurrentUsernameProvider;
import com.example.tracking_reporting.service.ReportService;
import com.example.tracking_reporting.service.report.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {

    private static final String REPORT_NOT_FOUND = "Report not found";

    private final ReportRepository reportRepository;
    private final EntityFinder entityFinder;
    private final MetricsQueryService metricsQueryService;
    private final S3StorageService s3StorageService;
    private final ReportSearchService reportSearchService;
    private final AuditLogService auditLogService;
    private final CurrentUsernameProvider currentUsernameProvider;
    private final List<ReportFileGenerator> generators;

    @Override
    public GeneratedReportResponse generate(GenerateReportRequest request) {
        String username = currentUsernameProvider.getCurrentUsername();
        auditLogService.save("GENERATE_REPORT_REQUEST", username, "Report", null,
                "scope=" + request.scope() + ", format=" + request.format() + ", period=" + request.periodLabel());

        Project project;
        Iteration iteration = null;

        if (request.scope() == ReportScope.PROJECT) {
            if (request.projectId() == null) {
                throw new IllegalArgumentException("projectId is required for PROJECT scope");
            }
            project = entityFinder.getProject(request.projectId());
        } else {
            if (request.iterationId() == null) {
                throw new IllegalArgumentException("iterationId is required for ITERATION scope");
            }
            iteration = entityFinder.getIteration(request.iterationId());
            project = iteration.getProject();
        }

        List<ReportMetricRow> metrics = metricsQueryService.loadProjectMetrics(project.getId(), request.periodLabel());
        ReportDataBundle dataBundle = new ReportDataBundle(
                project.getName(),
                iteration != null ? iteration.getName() : "-",
                request.scope().name(),
                request.periodLabel(),
                metrics
        );

        GeneratedBinaryFile binaryFile = pickGenerator(request.format()).generate(request.title(), dataBundle);
        S3StorageService.UploadResult uploadResult = s3StorageService.upload(binaryFile);

        Report report = new Report();
        report.setTitle(request.title());
        report.setScope(request.scope());
        report.setDocumentFormat(request.format());
        report.setReportStatus(ReportStatus.GENERATED);
        report.setProject(project);
        report.setIteration(iteration);
        report.setPeriodLabel(request.periodLabel());
        report.setS3Key(uploadResult.key());
        report.setFileUrl(uploadResult.url());
        report.setContentType(uploadResult.contentType());
        report.setFileSize(uploadResult.fileSize());
        report.setCreatedBy(username);

        Report saved = reportRepository.save(report);
        reportSearchService.index(saved);

        auditLogService.save("GENERATE_REPORT_SUCCESS", username, "Report", saved.getId().toString(), saved.getFileUrl());

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public GeneratedReportResponse getById(UUID id) {
        return toResponse(findByIdOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GeneratedReportResponse> getAll() {
        return reportRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportSearchItem> search(String query) {
        return reportSearchService.search(query);
    }

    @Override
    public GeneratedReportResponse update(UUID id, UpdateGeneratedReportRequest request) {
        Report report = findByIdOrThrow(id);
        report.setTitle(request.title());
        report.setPeriodLabel(request.periodLabel());

        Report saved = reportRepository.save(report);
        reportSearchService.index(saved);

        auditLogService.save("UPDATE_REPORT", currentUsernameProvider.getCurrentUsername(), "Report", saved.getId().toString(), "updated metadata");

        return toResponse(saved);
    }

    @Override
    public void delete(UUID id) {
        Report report = findByIdOrThrow(id);

        s3StorageService.delete(report.getS3Key());
        reportSearchService.delete(report.getId());

        report.setReportStatus(ReportStatus.DELETED);
        reportRepository.delete(report);

        auditLogService.save("DELETE_REPORT", currentUsernameProvider.getCurrentUsername(), "Report", id.toString(), "deleted report + s3 object + search index");
    }

    private Report findByIdOrThrow(UUID id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(REPORT_NOT_FOUND));
    }

    private ReportFileGenerator pickGenerator(com.example.tracking_reporting.enums.DocumentFormat format) {
        Map<com.example.tracking_reporting.enums.DocumentFormat, ReportFileGenerator> byFormat =
                generators.stream().collect(Collectors.toMap(ReportFileGenerator::supports, Function.identity()));

        ReportFileGenerator generator = byFormat.get(format);
        if (generator == null) {
            throw new IllegalStateException("No file generator found for format: " + format);
        }
        return generator;
    }

    private GeneratedReportResponse toResponse(Report report) {
        return new GeneratedReportResponse(
                report.getId(),
                report.getTitle(),
                report.getScope(),
                report.getDocumentFormat(),
                report.getReportStatus(),
                report.getProject() != null ? report.getProject().getId() : null,
                report.getIteration() != null ? report.getIteration().getId() : null,
                report.getPeriodLabel(),
                report.getS3Key(),
                report.getFileUrl(),
                report.getContentType(),
                report.getFileSize(),
                report.getCreatedBy(),
                report.getCreatedAt(),
                report.getUpdatedAt()
        );
    }
}