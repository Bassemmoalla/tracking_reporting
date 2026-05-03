package com.example.tracking_reporting.service.report;

import com.example.tracking_reporting.config.GitLabProperties;
import com.example.tracking_reporting.dto.report.ReportMetricRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GitLabMetricsClient {

    private final GitLabProperties gitLabProperties;

    public List<ReportMetricRow> loadProjectMetrics(UUID projectId, String periodLabel) {
        return List.of(
                new ReportMetricRow("gitlab_commits", "0"),
                new ReportMetricRow("gitlab_merge_requests", "0"),
                new ReportMetricRow("gitlab_issues", "0"),
                new ReportMetricRow("gitlab_period", periodLabel),
                new ReportMetricRow("gitlab_base_url", gitLabProperties.baseUrl())
        );
    }
}