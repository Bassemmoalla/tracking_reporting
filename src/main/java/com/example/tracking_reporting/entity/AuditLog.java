package com.example.tracking_reporting.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 80)
    private String action;

    @Column(nullable = false, length = 150)
    private String actor;

    @Column(nullable = false, length = 150)
    private String resourceType;

    @Column(length = 100)
    private String resourceId;

    @Column(length = 1000)
    private String details;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}