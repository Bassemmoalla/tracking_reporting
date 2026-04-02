package com.example.tracking_reporting.controller;

import com.example.tracking_reporting.dto.*;
import com.example.tracking_reporting.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) { this.teamService = teamService; }

    @PostMapping
    public TeamResponse create(@Valid @RequestBody CreateTeamRequest request) {
        return teamService.create(request);
    }

    @PostMapping("/assign-user")
    public TeamAssignmentResponse assignUser(@Valid @RequestBody AssignUserToTeamRequest request) {
        return teamService.assignUser(request);
    }

    @GetMapping
    public List<TeamResponse> getAll() { return teamService.getAll(); }

    @GetMapping("/{id}")
    public TeamResponse getById(@PathVariable UUID id) { return teamService.getById(id); }

    @PutMapping("/{id}")
    public TeamResponse update(@PathVariable UUID id,
                               @Valid @RequestBody UpdateTeamRequest request) {
        return teamService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        teamService.delete(id);
    }

    // unassign (sets unassignedAt)
    @DeleteMapping("/{teamId}/users/{userId}")
    public TeamAssignmentResponse unassign(@PathVariable UUID teamId, @PathVariable UUID userId) {
        return teamService.unassignUser(teamId, userId);
    }
}