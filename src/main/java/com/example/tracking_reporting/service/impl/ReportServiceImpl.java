package com.example.tracking_reporting.service.impl;

import com.example.tracking_reporting.dto.ReportRequest;
import com.example.tracking_reporting.dto.ReportResponse;
import com.example.tracking_reporting.entity.Project;
import com.example.tracking_reporting.entity.Report;
import com.example.tracking_reporting.exception.ResourceNotFoundException;
import com.example.tracking_reporting.helper.EntityFinder;
import com.example.tracking_reporting.mapper.ReportMapper;
import com.example.tracking_reporting.repository.ReportRepository;
import com.example.tracking_reporting.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final EntityFinder entityFinder;
    private final ReportMapper reportMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ReportResponse> getAll() {
        return reportRepository.findAll()
                .stream()
                .map(reportMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReportResponse getById(UUID id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + id));
        return reportMapper.toResponse(report);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportResponse> getByProjectId(UUID projectId) {
        return reportRepository.findByProjectId(projectId)
                .stream()
                .map(reportMapper::toResponse)
                .toList();
    }

    @Override
    public ReportResponse create(ReportRequest request) {
        Project project = entityFinder.getProject(request.projectId());

        Report report = Report.builder()
                .format(request.format())
                .generatedAt(request.generatedAt())
                .filePath(request.filePath())
                .validatedAt(request.validatedAt())
                .project(project)
                .build();

        return reportMapper.toResponse(reportRepository.save(report));
    }

    @Override
    public ReportResponse update(UUID id, ReportRequest request) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + id));

        Project project = entityFinder.getProject(request.projectId());

        report.setFormat(request.format());
        report.setGeneratedAt(request.generatedAt());
        report.setFilePath(request.filePath());
        report.setValidatedAt(request.validatedAt());
        report.setProject(project);

        return reportMapper.toResponse(reportRepository.save(report));
    }

    @Override
    public void delete(UUID id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + id));
        reportRepository.delete(report);
    }
}