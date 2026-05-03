package com.example.tracking_reporting.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest5_client.Rest5ClientTransport;
import co.elastic.clients.transport.rest5_client.low_level.Rest5Client;
import org.apache.hc.core5.http.HttpHost;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
@EnableConfigurationProperties({
        AwsProperties.class,
        AwsS3Properties.class,
        ElasticsearchProperties.class,
        GitLabProperties.class
})
public class StorageAndSearchConfig {

    @Bean
    public S3Client s3Client(AwsProperties awsProperties, AwsS3Properties s3Properties) {
        return S3Client.builder()
                .region(Region.of(awsProperties.region()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        s3Properties.accessKey(),
                                        s3Properties.secretKey()
                                )
                        )
                )
                .endpointOverride(URI.create(s3Properties.endpoint()))
                .forcePathStyle(true)
                .build();
    }

    @Bean(destroyMethod = "close")
    public Rest5Client elasticRest5Client(ElasticsearchProperties properties) {
        URI uri = URI.create(properties.url());
        return Rest5Client.builder(
                new HttpHost(uri.getScheme(), uri.getHost(), uri.getPort())
        ).build();
    }

    @Bean
    public ElasticsearchTransport elasticsearchTransport(Rest5Client rest5Client) {
        return new Rest5ClientTransport(rest5Client, new JacksonJsonpMapper());
    }

    @Bean
    public ElasticsearchClient elasticsearchClient(ElasticsearchTransport transport) {
        return new ElasticsearchClient(transport);
    }
}
