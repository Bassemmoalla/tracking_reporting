package com.example.tracking_reporting.service.impl;

import com.example.tracking_reporting.dto.ProjectRequest;
import com.example.tracking_reporting.dto.ProjectResponse;
import com.example.tracking_reporting.entity.Project;
import com.example.tracking_reporting.entity.Team;
import com.example.tracking_reporting.helper.EntityFinder;
import com.example.tracking_reporting.mapper.ProjectMapper;
import com.example.tracking_reporting.repository.IterationRepository;
import com.example.tracking_reporting.repository.ProjectRepository;
import com.example.tracking_reporting.repository.ReportRepository;
import com.example.tracking_reporting.repository.TaskRepository;
import com.example.tracking_reporting.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final IterationRepository iterationRepository;
    private final ReportRepository reportRepository;
    private final EntityFinder entityFinder;
    private final ProjectMapper projectMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getAll() {
        return projectRepository.findAll()
                .stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getById(UUID id) {
        return projectMapper.toResponse(getEntityById(id));
    }

    @Override
    public ProjectResponse create(ProjectRequest request) {
        Team team = entityFinder.getTeam(request.teamId());

        Project project = Project.builder()
                .name(request.name())
                .description(request.description())
                .status(request.status())
                .deadline(request.deadline())
                .team(team)
                .build();

        return projectMapper.toResponse(projectRepository.save(project));
    }

    @Override
    public ProjectResponse update(UUID id, ProjectRequest request) {
        Project project = getEntityById(id);
        Team team = entityFinder.getTeam(request.teamId());

        project.setName(request.name());
        project.setDescription(request.description());
        project.setStatus(request.status());
        project.setDeadline(request.deadline());
        project.setTeam(team);

        return projectMapper.toResponse(projectRepository.save(project));
    }

    @Override
    public void delete(UUID id) {
        Project project = getEntityById(id);

        taskRepository.deleteByProjectId(id);
        reportRepository.deleteByProjectId(id);
        iterationRepository.deleteByProjectId(id);
        projectRepository.delete(project);
    }

    @Override
    @Transactional(readOnly = true)
    public Project getEntityById(UUID id) {
        return entityFinder.getProject(id);
    }
}