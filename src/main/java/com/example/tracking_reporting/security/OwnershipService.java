package com.example.tracking_reporting.security;

import com.example.tracking_reporting.entity.Project;
import com.example.tracking_reporting.entity.User;
import com.example.tracking_reporting.exception.AccessDeniedException;
import com.example.tracking_reporting.repository.TeamAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnershipService {

    private final CurrentUserService currentUserService;
    private final TeamAssignmentRepository teamAssignmentRepository;

    public void checkCanAccessProject(Project project) {
        if (project == null || project.getTeam() == null || project.getTeam().getId() == null) {
            throw new AccessDeniedException("Project team information is missing");
        }

        if (isAdmin()) {
            return;
        }

        User currentUser = currentUserService.getCurrentUser();

        boolean isMemberOfTeam = teamAssignmentRepository
                .findByUserIdAndTeamId(currentUser.getId(), project.getTeam().getId())
                .isPresent();

        if (!isMemberOfTeam) {
            throw new AccessDeniedException("You are not allowed to access this project");
        }
    }

    public void checkCanManageProject(Project project) {
        if (project == null || project.getTeam() == null || project.getTeam().getId() == null) {
            throw new AccessDeniedException("Project team information is missing");
        }

        if (isAdmin()) {
            return;
        }

        User currentUser = currentUserService.getCurrentUser();

        boolean isMemberOfTeam = teamAssignmentRepository
                .findByUserIdAndTeamId(currentUser.getId(), project.getTeam().getId())
                .isPresent();

        if (!isMemberOfTeam) {
            throw new AccessDeniedException("You are not allowed to manage this project");
        }
    }

    public void checkCanCreateProjectForTeam(java.util.UUID teamId) {
        if (teamId == null) {
            throw new AccessDeniedException("Team id is required");
        }

        if (isAdmin()) {
            return;
        }

        User currentUser = currentUserService.getCurrentUser();

        boolean isMemberOfTeam = teamAssignmentRepository
                .findByUserIdAndTeamId(currentUser.getId(), teamId)
                .isPresent();

        if (!isMemberOfTeam) {
            throw new AccessDeniedException("You are not allowed to create a project for this team");
        }
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(a ->
                        "platform:configure".equals(a.getAuthority())
                                || "user:delete".equals(a.getAuthority())
                                || "user:create".equals(a.getAuthority())
                );
    }
}