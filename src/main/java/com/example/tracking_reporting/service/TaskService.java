package com.example.tracking_reporting.service;

import com.example.tracking_reporting.dto.TaskRequest;
import com.example.tracking_reporting.dto.TaskResponse;
import com.example.tracking_reporting.entity.Task;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    List<TaskResponse> getAll();
    TaskResponse getById(UUID id);
    List<TaskResponse> getByProjectId(UUID projectId);
    TaskResponse create(TaskRequest request);
    TaskResponse update(UUID id, TaskRequest request);
    void delete(UUID id);
    Task getEntityById(UUID id);
}