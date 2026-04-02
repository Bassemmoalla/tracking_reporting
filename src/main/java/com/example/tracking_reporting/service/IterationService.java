package com.example.tracking_reporting.service;

import com.example.tracking_reporting.dto.IterationRequest;
import com.example.tracking_reporting.dto.IterationResponse;
import com.example.tracking_reporting.entity.Iteration;

import java.util.List;
import java.util.UUID;

public interface IterationService {
    List<IterationResponse> getAll();
    IterationResponse getById(UUID id);
    List<IterationResponse> getByProjectId(UUID projectId);
    IterationResponse create(IterationRequest request);
    IterationResponse update(UUID id, IterationRequest request);
    void delete(UUID id);
    Iteration getEntityById(UUID id);
}