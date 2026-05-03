package com.example.tracking_reporting.service.report;

import com.example.tracking_reporting.config.AwsS3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3StorageService {

    private final S3Client s3Client;
    private final AwsS3Properties properties;

    public UploadResult upload(GeneratedBinaryFile file) {
        String key = properties.baseFolder() + "/" + Instant.now().toEpochMilli() + "-" + UUID.randomUUID() + "-" + file.filename();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(properties.bucket())
                .key(key)
                .contentType(file.contentType())
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(file.content()));

        String url = "https://" + properties.bucket() + ".s3.amazonaws.com/" + key;
        return new UploadResult(key, url, file.content().length, file.contentType());
    }

    public void delete(String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(properties.bucket())
                .key(key)
                .build());
    }

    public record UploadResult(String key, String url, long fileSize, String contentType) {
    }
}