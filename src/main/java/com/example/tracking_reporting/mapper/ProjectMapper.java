package com.example.tracking_reporting.mapper;

import com.example.tracking_reporting.dto.ProjectResponse;
import com.example.tracking_reporting.entity.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStatus(),
                project.getDeadline(),
                project.getTeam().getId(),
                project.getTeam().getName(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}