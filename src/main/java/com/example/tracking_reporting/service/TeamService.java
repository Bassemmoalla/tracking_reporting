package com.example.tracking_reporting.service;

import com.example.tracking_reporting.dto.AssignUserToTeamRequest;
import com.example.tracking_reporting.dto.CreateTeamRequest;
import com.example.tracking_reporting.dto.TeamAssignmentResponse;
import com.example.tracking_reporting.dto.TeamResponse;
import com.example.tracking_reporting.dto.UpdateTeamRequest;
import com.example.tracking_reporting.entity.Team;
import com.example.tracking_reporting.entity.TeamAssignment;
import com.example.tracking_reporting.entity.User;
import com.example.tracking_reporting.exception.ResourceNotFoundException;
import com.example.tracking_reporting.repository.TeamAssignmentRepository;
import com.example.tracking_reporting.repository.TeamRepository;
import com.example.tracking_reporting.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TeamService {

    private static final String TEAM_NOT_FOUND = "Team not found";
    private static final String USER_NOT_FOUND = "User not found";
    private static final String USER_ALREADY_ASSIGNED_TO_TEAM = "User already assigned to this team";
    private static final String ASSIGNMENT_NOT_FOUND = "Assignment not found";

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamAssignmentRepository teamAssignmentRepository;

    public TeamService(TeamRepository teamRepository,
                       UserRepository userRepository,
                       TeamAssignmentRepository teamAssignmentRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.teamAssignmentRepository = teamAssignmentRepository;
    }

    public TeamResponse create(CreateTeamRequest request) {
        Team team = new Team();
        team.setName(request.name());
        team.setDescription(request.description());
        Team saved = teamRepository.save(team);
        return new TeamResponse(saved.getId(), saved.getName(), saved.getDescription());
    }

    public TeamAssignmentResponse assignUser(AssignUserToTeamRequest request) {
        Team team = teamRepository.findById(request.teamId())
                .orElseThrow(() -> new ResourceNotFoundException(TEAM_NOT_FOUND));

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        teamAssignmentRepository.findByTeam_IdAndUser_Id(request.teamId(), request.userId())
                .ifPresent(existing -> {
                    throw new IllegalStateException(USER_ALREADY_ASSIGNED_TO_TEAM);
                });

        TeamAssignment assignment = new TeamAssignment();
        assignment.setTeam(team);
        assignment.setUser(user);
        assignment.setAssignedByUserId(request.assignedByUserId());

        TeamAssignment saved = teamAssignmentRepository.save(assignment);

        return new TeamAssignmentResponse(
                saved.getId(),
                team.getId(),
                user.getId(),
                saved.getAssignedAt(),
                saved.getAssignedByUserId()
        );
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> getAll() {
        return teamRepository.findAll().stream()
                .map(team -> new TeamResponse(team.getId(), team.getName(), team.getDescription()))
                .toList();
    }

    @Transactional(readOnly = true)
    public TeamResponse getById(UUID id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TEAM_NOT_FOUND));

        return new TeamResponse(team.getId(), team.getName(), team.getDescription());
    }

    @Transactional
    public TeamResponse update(UUID id, UpdateTeamRequest request) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TEAM_NOT_FOUND));

        team.setName(request.name());
        team.setDescription(request.description());

        Team saved = teamRepository.save(team);
        return new TeamResponse(saved.getId(), saved.getName(), saved.getDescription());
    }

    @Transactional
    public void delete(UUID id) {
        teamAssignmentRepository.deleteAllByTeam_Id(id);
        teamRepository.deleteById(id);
    }

    @Transactional
    public TeamAssignmentResponse unassignUser(UUID teamId, UUID userId) {
        TeamAssignment assignment = teamAssignmentRepository.findByTeam_IdAndUser_Id(teamId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(ASSIGNMENT_NOT_FOUND));

        assignment.setUnassignedAt(Instant.now());
        TeamAssignment saved = teamAssignmentRepository.save(assignment);

        return new TeamAssignmentResponse(
                saved.getId(),
                teamId,
                userId,
                saved.getAssignedAt(),
                saved.getAssignedByUserId()
        );
    }

    public Team getEntityById(UUID id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));
    }
}