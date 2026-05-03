package com.example.tracking_reporting.service.impl;

import com.example.tracking_reporting.dto.TaskRequest;
import com.example.tracking_reporting.dto.TaskResponse;
import com.example.tracking_reporting.entity.Iteration;
import com.example.tracking_reporting.entity.Project;
import com.example.tracking_reporting.entity.Task;
import com.example.tracking_reporting.entity.User;
import com.example.tracking_reporting.exception.ResourceNotFoundException;
import com.example.tracking_reporting.helper.EntityFinder;
import com.example.tracking_reporting.mapper.TaskMapper;
import com.example.tracking_reporting.repository.TaskRepository;
import com.example.tracking_reporting.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final EntityFinder entityFinder;
    private final TaskMapper taskMapper;
    private static final String TASK_NOT_FOUND_WITH_ID = "Task not found with id: ";

    private Task findTaskByIdOrThrow(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND_WITH_ID + id));
    }
    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getAll() {
        return taskRepository.findAll()
                .stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getById(UUID id) {
        return taskMapper.toResponse(findTaskByIdOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getByProjectId(UUID projectId) {
        return taskRepository.findByProjectId(projectId)
                .stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    @Override
    public TaskResponse create(TaskRequest request) {
        Project project = entityFinder.getProject(request.projectId());

        User assignedUser = null;
        if (request.assignedUserId() != null) {
            assignedUser = entityFinder.getUser(request.assignedUserId());
        }

        Iteration iteration = null;
        if (request.iterationId() != null) {
            iteration = entityFinder.getIteration(request.iterationId());
        }

        Task task = Task.builder()
                .name(request.name())
                .shortText(request.shortText())
                .endTaskDate(request.endTaskDate())
                .description(request.description())
                .project(project)
                .assignedUser(assignedUser)
                .iteration(iteration)
                .build();

        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    public TaskResponse update(UUID id, TaskRequest request) {
        Task task = findTaskByIdOrThrow(id);
        Project project = entityFinder.getProject(request.projectId());

        User assignedUser = null;
        if (request.assignedUserId() != null) {
            assignedUser = entityFinder.getUser(request.assignedUserId());
        }

        Iteration iteration = null;
        if (request.iterationId() != null) {
            iteration = entityFinder.getIteration(request.iterationId());
        }

        task.setName(request.name());
        task.setShortText(request.shortText());
        task.setEndTaskDate(request.endTaskDate());
        task.setDescription(request.description());
        task.setProject(project);
        task.setAssignedUser(assignedUser);
        task.setIteration(iteration);

        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    public void delete(UUID id) {
        Task task = findTaskByIdOrThrow(id);
        taskRepository.delete(task);
    }

    @Override
    @Transactional(readOnly = true)
    public Task getEntityById(UUID id) {
        return findTaskByIdOrThrow(id);
    }
}