package com.example.tracking_reporting.helper;

import com.example.tracking_reporting.entity.Iteration;
import com.example.tracking_reporting.entity.Project;
import com.example.tracking_reporting.entity.Team;
import com.example.tracking_reporting.entity.User;
import com.example.tracking_reporting.exception.ResourceNotFoundException;
import com.example.tracking_reporting.repository.IterationRepository;
import com.example.tracking_reporting.repository.ProjectRepository;
import com.example.tracking_reporting.repository.TeamRepository;
import com.example.tracking_reporting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EntityFinder {

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final IterationRepository iterationRepository;

    public Project getProject(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }

    public Team getTeam(UUID id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));
    }

    public User getUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public Iteration getIteration(UUID id) {
        return iterationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Iteration not found with id: " + id));
    }
}