package com.example.tracking_reporting.mapper;

import com.example.tracking_reporting.dto.ReportResponse;
import com.example.tracking_reporting.entity.Report;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

    public ReportResponse toResponse(Report report) {
        return new ReportResponse(
                report.getId(),
                report.getFormat(),
                report.getGeneratedAt(),
                report.getFilePath(),
                report.getValidatedAt(),
                report.getProject().getId(),
                report.getProject().getName()
        );
    }
}
