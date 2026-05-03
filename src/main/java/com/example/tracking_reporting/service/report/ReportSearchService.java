package com.example.tracking_reporting.service.report;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.tracking_reporting.config.ElasticsearchProperties;
import com.example.tracking_reporting.dto.report.ReportSearchItem;
import com.example.tracking_reporting.entity.Report;
import com.example.tracking_reporting.enums.DocumentFormat;
import com.example.tracking_reporting.enums.ReportScope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportSearchService {

    private final ElasticsearchClient elasticsearchClient;
    private final ElasticsearchProperties elasticsearchProperties;

    public void index(Report report) {
        try {
            IndexRequest<Map<String, Object>> request = IndexRequest.of(i -> i
                    .index(elasticsearchProperties.reportIndex())
                    .id(report.getId().toString())
                    .refresh(Refresh.True)
                    .document(Map.of(
                            "id", report.getId().toString(),
                            "title", report.getTitle(),
                            "scope", report.getScope().name(),
                            "format", report.getDocumentFormat().name(),
                            "projectId", report.getProject() != null ? report.getProject().getId().toString() : "",
                            "iterationId", report.getIteration() != null ? report.getIteration().getId().toString() : "",
                            "periodLabel", report.getPeriodLabel(),
                            "fileUrl", report.getFileUrl(),
                            "createdBy", report.getCreatedBy(),
                            "createdAt", report.getCreatedAt().toString()
                    )));

            elasticsearchClient.index(request);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to index report in Elasticsearch", ex);
        }
    }

    public void delete(UUID reportId) {
        try {
            DeleteRequest request = DeleteRequest.of(d -> d
                    .index(elasticsearchProperties.reportIndex())
                    .id(reportId.toString())
                    .refresh(Refresh.True));
            elasticsearchClient.delete(request);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to delete report from Elasticsearch", ex);
        }
    }

    public List<ReportSearchItem> search(String queryText) {
        try {
            Query query = Query.of(q -> q
                    .multiMatch(m -> m
                            .query(queryText)
                            .fields("title", "periodLabel", "scope", "format", "createdBy")));

            SearchResponse<Map> response = elasticsearchClient.search(s -> s
                    .index(elasticsearchProperties.reportIndex())
                    .query(query)
                    .size(50), Map.class);

            List<ReportSearchItem> items = new ArrayList<>();
            response.hits().hits().forEach(hit -> {
                Map<?, ?> source = hit.source();
                if (source == null) {
                    return;
                }
                items.add(new ReportSearchItem(
                        UUID.fromString((String) source.get("id")),
                        (String) source.get("title"),
                        ReportScope.valueOf((String) source.get("scope")),
                        DocumentFormat.valueOf((String) source.get("format")),
                        parseUuid((String) source.get("projectId")),
                        parseUuid((String) source.get("iterationId")),
                        (String) source.get("periodLabel"),
                        (String) source.get("fileUrl"),
                        (String) source.get("createdBy"),
                        Instant.parse((String) source.get("createdAt"))
                ));
            });

            return items;
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to search reports in Elasticsearch", ex);
        }
    }

    private UUID parseUuid(String value) {
        return value == null || value.isBlank() ? null : UUID.fromString(value);
    }
}