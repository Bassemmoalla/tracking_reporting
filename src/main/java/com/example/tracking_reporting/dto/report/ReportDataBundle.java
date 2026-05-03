package com.example.tracking_reporting.dto.report;

import java.util.List;

public record ReportDataBundle(
        String projectName,
        String iterationName,
        String scopeLabel,
        String periodLabel,
        List<ReportMetricRow> metrics
) {
}