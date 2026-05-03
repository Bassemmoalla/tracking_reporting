package com.example.tracking_reporting.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.aws.s3")
public record AwsS3Properties(
        String bucket,
        String baseFolder,
        String accessKey,
        String secretKey,
        String endpoint
) {
}