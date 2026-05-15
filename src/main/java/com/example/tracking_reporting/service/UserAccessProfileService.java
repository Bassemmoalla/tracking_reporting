package com.example.tracking_reporting.service;

import com.example.tracking_reporting.entity.Permission;
import com.example.tracking_reporting.entity.PermissionGroup;
import com.example.tracking_reporting.entity.User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserAccessProfileService {

    public Set<String> extractPermissionKeys(User user) {
        if (user == null || user.getPermissions() == null) {
            return new LinkedHashSet<>();
        }

        return user.getPermissions().stream()
                .map(Permission::getPermissionKey)
                .filter(value -> value != null && !value.isBlank())
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<String> extractPermissionGroupNames(User user) {
        if (user == null || user.getPermissionGroups() == null) {
            return new LinkedHashSet<>();
        }

        return user.getPermissionGroups().stream()
                .map(PermissionGroup::getName)
                .filter(value -> value != null && !value.isBlank())
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<String> resolveRoleLabels(User user) {
        return resolveRoleLabels(extractPermissionKeys(user), extractPermissionGroupNames(user));
    }

    public Set<String> resolveRoleLabels(Collection<String> permissionKeys, Collection<String> permissionGroupNames) {
        LinkedHashSet<String> roles = new LinkedHashSet<>();

        if (permissionGroupNames != null) {
            for (String groupName : permissionGroupNames) {
                addRoleFromGroupName(roles, groupName);
            }
        }

        if (roles.isEmpty()) {
            if (hasAny(permissionKeys,
                    "user:create", "user:update", "user:delete",
                    "role:create", "role:update", "role:delete",
                    "platform:configure")) {
                roles.add("Admin");
            }

            if (hasAny(permissionKeys,
                    "team:create", "team:update",
                    "project:create", "project:update",
                    "iteration:create", "iteration:update",
                    "report:validate")) {
                roles.add("Team Leader");
            }

            if (hasAny(permissionKeys,
                    "task:update", "note:create", "note:update")) {
                roles.add("Team Member");
            }

            if (roles.isEmpty() && permissionKeys != null && !permissionKeys.isEmpty()) {
                roles.add("Observer");
            }
        }

        if (roles.isEmpty() && permissionGroupNames != null && !permissionGroupNames.isEmpty()) {
            roles.addAll(permissionGroupNames.stream()
                    .filter(value -> value != null && !value.isBlank())
                    .collect(Collectors.toCollection(LinkedHashSet::new)));
        }

        if (roles.isEmpty()) {
            roles.add("Unassigned");
        }

        return roles;
    }

    private void addRoleFromGroupName(Set<String> roles, String groupName) {
        if (groupName == null || groupName.isBlank()) {
            return;
        }

        String normalized = groupName.trim().toLowerCase();

        if (normalized.contains("admin")) {
            roles.add("Admin");
            return;
        }

        if (normalized.contains("team leader")
                || normalized.contains("leader")
                || normalized.contains("project manager")) {
            roles.add("Team Leader");
            return;
        }

        if (normalized.contains("observer")) {
            roles.add("Observer");
            return;
        }

        if (normalized.contains("team member") || normalized.contains("member")) {
            roles.add("Team Member");
        }
    }

    private boolean hasAny(Collection<String> source, String... expected) {
        if (source == null || source.isEmpty()) {
            return false;
        }

        Set<String> normalized = source.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .collect(Collectors.toSet());

        for (String value : expected) {
            if (normalized.contains(value)) {
                return true;
            }
        }

        return false;
    }
}