package com.example.tracking_reporting.entity;

import com.example.tracking_reporting.enums.DocumentFormat;
import com.example.tracking_reporting.enums.ReportScope;
import com.example.tracking_reporting.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reports")
@Getter
@Setter
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportScope scope;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DocumentFormat documentFormat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportStatus reportStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iteration_id")
    private Iteration iteration;

    @Column(nullable = false, length = 100)
    private String periodLabel;

    @Column(nullable = false, length = 500)
    private String s3Key;

    @Column(nullable = false, length = 1000)
    private String fileUrl;

    @Column(nullable = false, length = 120)
    private String contentType;

    @Column(nullable = false)
    private long fileSize;

    @Column(nullable = false, length = 150)
    private String createdBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }
}