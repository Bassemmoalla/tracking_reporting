package com.example.tracking_reporting.service;

import com.example.tracking_reporting.dto.ProjectRequest;
import com.example.tracking_reporting.dto.ProjectResponse;
import com.example.tracking_reporting.entity.Project;

import java.util.List;
import java.util.UUID;

public interface ProjectService {
    List<ProjectResponse> getAll();

    ProjectResponse getById(UUID id);

    ProjectResponse create(ProjectRequest request);

    ProjectResponse update(UUID id, ProjectRequest request);

    ProjectResponse archive(UUID id);

    ProjectResponse restore(UUID id);

    void delete(UUID id);

    Project getEntityById(UUID id);
}