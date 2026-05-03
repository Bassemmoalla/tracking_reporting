package com.example.tracking_reporting.service.report;

import com.example.tracking_reporting.dto.report.ReportDataBundle;
import com.example.tracking_reporting.enums.DocumentFormat;

public interface ReportFileGenerator {
    DocumentFormat supports();
    GeneratedBinaryFile generate(String title, ReportDataBundle dataBundle);
}