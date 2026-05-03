package com.example.tracking_reporting.service.report;

public record GeneratedBinaryFile(
        String filename,
        String contentType,
        byte[] content
) {
}