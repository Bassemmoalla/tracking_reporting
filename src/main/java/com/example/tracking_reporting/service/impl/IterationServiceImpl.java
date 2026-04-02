package com.example.tracking_reporting.service.impl;

import com.example.tracking_reporting.dto.IterationRequest;
import com.example.tracking_reporting.dto.IterationResponse;
import com.example.tracking_reporting.entity.Iteration;
import com.example.tracking_reporting.entity.Project;
import com.example.tracking_reporting.helper.EntityFinder;
import com.example.tracking_reporting.mapper.IterationMapper;
import com.example.tracking_reporting.repository.IterationRepository;
import com.example.tracking_reporting.repository.TaskRepository;
import com.example.tracking_reporting.service.IterationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class IterationServiceImpl implements IterationService {

    private final IterationRepository iterationRepository;
    private final TaskRepository taskRepository;
    private final EntityFinder entityFinder;
    private final IterationMapper iterationMapper;

    @Override
    @Transactional(readOnly = true)
    public List<IterationResponse> getAll() {
        return iterationRepository.findAll()
                .stream()
                .map(iterationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public IterationResponse getById(UUID id) {
        return iterationMapper.toResponse(getEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<IterationResponse> getByProjectId(UUID projectId) {
        return iterationRepository.findByProjectId(projectId)
                .stream()
                .map(iterationMapper::toResponse)
                .toList();
    }

    @Override
    public IterationResponse create(IterationRequest request) {
        Project project = entityFinder.getProject(request.projectId());

        Iteration iteration = Iteration.builder()
                .name(request.name())
                .plannedBudget(request.plannedBudget())
                .realBudget(request.realBudget())
                .estimatedFinishDate(request.estimatedFinishDate())
                .realFinishDate(request.realFinishDate())
                .objective(request.objective())
                .status(request.status())
                .project(project)
                .build();

        return iterationMapper.toResponse(iterationRepository.save(iteration));
    }

    @Override
    public IterationResponse update(UUID id, IterationRequest request) {
        Iteration iteration = getEntityById(id);
        Project project = entityFinder.getProject(request.projectId());

        iteration.setName(request.name());
        iteration.setPlannedBudget(request.plannedBudget());
        iteration.setRealBudget(request.realBudget());
        iteration.setEstimatedFinishDate(request.estimatedFinishDate());
        iteration.setRealFinishDate(request.realFinishDate());
        iteration.setObjective(request.objective());
        iteration.setStatus(request.status());
        iteration.setProject(project);

        return iterationMapper.toResponse(iterationRepository.save(iteration));
    }

    @Override
    public void delete(UUID id) {
        Iteration iteration = getEntityById(id);
        taskRepository.clearIterationFromTasks(id);
        iterationRepository.delete(iteration);
    }

    @Override
    @Transactional(readOnly = true)
    public Iteration getEntityById(UUID id) {
        return entityFinder.getIteration(id);
    }
}