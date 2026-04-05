package com.example.tracking_reporting.controller;

import com.example.tracking_reporting.dto.CreateTeamRequest;
import com.example.tracking_reporting.dto.TeamResponse;
import com.example.tracking_reporting.dto.UpdateTeamRequest;
import com.example.tracking_reporting.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @GetMapping
    @PreAuthorize("hasAuthority('team:view')")
    public List<TeamResponse> getAll() {
        return teamService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('team:view')")
    public TeamResponse getById(@PathVariable UUID id) {
        return teamService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('team:create')")
    public TeamResponse create(@Valid @RequestBody CreateTeamRequest request) {
        return teamService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('team:update')")
    public TeamResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateTeamRequest request) {
        return teamService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('team:delete')")
    public void delete(@PathVariable UUID id) {
        teamService.delete(id);
    }
}
