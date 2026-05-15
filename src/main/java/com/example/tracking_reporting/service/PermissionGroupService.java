package com.example.tracking_reporting.service;

import com.example.tracking_reporting.dto.AssignPermissionToGroupRequest;
import com.example.tracking_reporting.dto.CreatePermissionGroupRequest;
import com.example.tracking_reporting.dto.PermissionGroupResponse;
import com.example.tracking_reporting.dto.PermissionResponse;
import com.example.tracking_reporting.dto.UpdatePermissionGroupRequest;
import com.example.tracking_reporting.entity.Permission;
import com.example.tracking_reporting.entity.PermissionGroup;
import com.example.tracking_reporting.entity.User;
import com.example.tracking_reporting.repository.PermissionGroupRepository;
import com.example.tracking_reporting.repository.PermissionRepository;
import com.example.tracking_reporting.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PermissionGroupService {

    private static final String PERMISSION_GROUP_NOT_FOUND = "Permission group not found";
    private static final String PERMISSION_NOT_FOUND = "Permission not found";

    private final PermissionGroupRepository permissionGroupRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    public PermissionGroupService(PermissionGroupRepository permissionGroupRepository,
                                  PermissionRepository permissionRepository,
                                  UserRepository userRepository) {
        this.permissionGroupRepository = permissionGroupRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
    }

    public PermissionGroupResponse create(CreatePermissionGroupRequest request) {
        PermissionGroup group = new PermissionGroup();
        group.setName(request.name());
        group.setDescription(request.description());
        group.setPermissions(resolvePermissionsByKeys(request.permissionKeys()));

        return map(permissionGroupRepository.save(group));
    }

    @Transactional
    public PermissionGroupResponse assignPermission(AssignPermissionToGroupRequest request) {
        PermissionGroup group = permissionGroupRepository.findById(request.permissionGroupId())
                .orElseThrow(() -> new RuntimeException(PERMISSION_GROUP_NOT_FOUND));

        Permission permission = permissionRepository.findById(request.permissionId())
                .orElseThrow(() -> new RuntimeException(PERMISSION_NOT_FOUND));

        group.getPermissions().add(permission);
        permissionGroupRepository.saveAndFlush(group);

        return map(group);
    }

    @Transactional(readOnly = true)
    public List<PermissionGroupResponse> getAll() {
        return permissionGroupRepository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    @Transactional(readOnly = true)
    public PermissionGroupResponse getById(UUID id) {
        PermissionGroup group = permissionGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(PERMISSION_GROUP_NOT_FOUND));

        return map(group);
    }

    @Transactional
    public PermissionGroupResponse update(UUID id, UpdatePermissionGroupRequest request) {
        PermissionGroup group = permissionGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(PERMISSION_GROUP_NOT_FOUND));

        group.setName(request.name());
        group.setDescription(request.description());

        return map(permissionGroupRepository.save(group));
    }

    @Transactional
    public void delete(UUID id) {
        PermissionGroup group = permissionGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(PERMISSION_GROUP_NOT_FOUND));

        List<User> users = userRepository.findAllByPermissionGroups_Id(id);
        for (User user : users) {
            user.getPermissionGroups().removeIf(pg -> pg.getId().equals(id));
        }
        userRepository.saveAll(users);

        group.getPermissions().clear();
        permissionGroupRepository.saveAndFlush(group);

        permissionGroupRepository.delete(group);
    }

    private Set<Permission> resolvePermissionsByKeys(Set<String> permissionKeys) {
        if (permissionKeys == null || permissionKeys.isEmpty()) {
            return new HashSet<>();
        }

        Set<String> cleanedKeys = permissionKeys.stream()
                .filter(key -> key != null && !key.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (cleanedKeys.isEmpty()) {
            return new HashSet<>();
        }

        List<Permission> permissions = permissionRepository.findByPermissionKeyIn(cleanedKeys);

        Set<String> foundKeys = permissions.stream()
                .map(Permission::getPermissionKey)
                .collect(Collectors.toSet());

        Set<String> missingKeys = cleanedKeys.stream()
                .filter(key -> !foundKeys.contains(key))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (!missingKeys.isEmpty()) {
            throw new IllegalArgumentException("Invalid permission keys: " + String.join(", ", missingKeys));
        }

        return new HashSet<>(permissions);
    }

    private PermissionGroupResponse map(PermissionGroup group) {
        return new PermissionGroupResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getPermissions().stream()
                        .map(this::mapPermission)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
        );
    }

    private PermissionResponse mapPermission(Permission permission) {
        return new PermissionResponse(
                permission.getId(),
                permission.getPermissionKey(),
                permission.getName(),
                permission.getDescription(),
                permission.getModule()
        );
    }
}