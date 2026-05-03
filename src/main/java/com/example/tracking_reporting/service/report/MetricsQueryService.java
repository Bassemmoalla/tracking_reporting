package com.example.tracking_reporting.service.report;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.tracking_reporting.config.ElasticsearchProperties;
import com.example.tracking_reporting.dto.report.ReportMetricRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MetricsQueryService {

    private final ElasticsearchClient elasticsearchClient;
    private final ElasticsearchProperties elasticsearchProperties;
    private final GitLabMetricsClient gitLabMetricsClient;

    public List<ReportMetricRow> loadProjectMetrics(UUID projectId, String periodLabel) {
        List<ReportMetricRow> metrics = searchMetricsInElastic(projectId, periodLabel);

        if (!metrics.isEmpty()) {
            return metrics;
        }

        return gitLabMetricsClient.loadProjectMetrics(projectId, periodLabel);
    }

    private List<ReportMetricRow> searchMetricsInElastic(UUID projectId, String periodLabel) {
        try {
            Query query = BoolQuery.of(b -> b
                    .must(m -> m.term(t -> t.field("projectId.keyword").value(projectId.toString())))
                    .must(m -> m.term(t -> t.field("periodLabel.keyword").value(periodLabel)))
            )._toQuery();

            SearchResponse<Map> response = elasticsearchClient.search(s -> s
                            .index(elasticsearchProperties.metricsIndex())
                            .query(query)
                            .size(1),
                    Map.class);

            if (response.hits().hits().isEmpty()) {
                return List.of();
            }

            Map<String, Object> source = response.hits().hits().get(0).source();
            if (source == null) {
                return List.of();
            }

            List<ReportMetricRow> rows = new ArrayList<>();
            source.forEach((key, value) ->
                    rows.add(new ReportMetricRow(key, value == null ? "" : value.toString()))
            );

            return rows;

        } catch (ElasticsearchException ex) {
            if (isIndexNotFound(ex)) {
                return List.of();
            }
            throw new IllegalStateException("Failed to query Elasticsearch metrics", ex);

        } catch (IOException ex) {
            return List.of();
        }
    }

    private boolean isIndexNotFound(ElasticsearchException ex) {
        return ex.response() != null
                && ex.response().error() != null
                && "index_not_found_exception".equals(ex.response().error().type());
    }
}