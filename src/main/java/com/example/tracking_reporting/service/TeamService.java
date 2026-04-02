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
                .orElseThrow(() -> new RuntimeException("Team not found"));
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        teamAssignmentRepository.findByTeam_IdAndUser_Id(request.teamId(), request.userId())
                .ifPresent(existing -> { throw new RuntimeException("User already assigned to this team"); });

        TeamAssignment a = new TeamAssignment();
        a.setTeam(team);
        a.setUser(user);
        a.setAssignedByUserId(request.assignedByUserId());
        TeamAssignment saved = teamAssignmentRepository.save(a);

        return new TeamAssignmentResponse(saved.getId(), team.getId(), user.getId(), saved.getAssignedAt(), saved.getAssignedByUserId());
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> getAll() {
        return teamRepository.findAll().stream()
                .map(t -> new TeamResponse(t.getId(), t.getName(), t.getDescription()))
                .toList();
    }

    @Transactional(readOnly = true)
    public TeamResponse getById(UUID id) {
        Team t = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        return new TeamResponse(t.getId(), t.getName(), t.getDescription());
    }

    @Transactional
    public TeamResponse update(UUID id, UpdateTeamRequest request) {
        Team t = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        t.setName(request.name());
        t.setDescription(request.description());
        Team saved = teamRepository.save(t);
        return new TeamResponse(saved.getId(), saved.getName(), saved.getDescription());
    }

    @Transactional
    public void delete(UUID id) {
        // remove assignments first (FK)
        teamAssignmentRepository.deleteAllByTeam_Id(id);
        teamRepository.deleteById(id);
    }

    // "Delete" a membership logically: set unassignedAt
    @Transactional
    public TeamAssignmentResponse unassignUser(UUID teamId, UUID userId) {
        TeamAssignment a = teamAssignmentRepository.findByTeam_IdAndUser_Id(teamId, userId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        a.setUnassignedAt(Instant.now());
        TeamAssignment saved = teamAssignmentRepository.save(a);

        return new TeamAssignmentResponse(saved.getId(), teamId, userId, saved.getAssignedAt(), saved.getAssignedByUserId());
    }
    public Team getEntityById(UUID id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));
    }
}