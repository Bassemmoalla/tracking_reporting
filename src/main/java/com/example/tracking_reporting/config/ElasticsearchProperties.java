package com.example.tracking_reporting.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.elasticsearch")
public record ElasticsearchProperties(
        String url,
        String reportIndex,
        String metricsIndex
) {
}