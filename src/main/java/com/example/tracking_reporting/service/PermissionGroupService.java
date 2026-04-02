package com.example.tracking_reporting.service;

import com.example.tracking_reporting.dto.AssignPermissionToGroupRequest;
import com.example.tracking_reporting.dto.CreatePermissionGroupRequest;
import com.example.tracking_reporting.dto.PermissionGroupResponse;
import com.example.tracking_reporting.dto.UpdatePermissionGroupRequest;
import com.example.tracking_reporting.entity.Permission;
import com.example.tracking_reporting.entity.PermissionGroup;
import com.example.tracking_reporting.entity.User;
import com.example.tracking_reporting.repository.PermissionGroupRepository;
import com.example.tracking_reporting.repository.PermissionRepository;
import com.example.tracking_reporting.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PermissionGroupService {

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
        return map(permissionGroupRepository.save(group));
    }

    @Transactional
    public PermissionGroupResponse assignPermission(AssignPermissionToGroupRequest request) {
        PermissionGroup group = permissionGroupRepository.findById(request.permissionGroupId())
                .orElseThrow(() -> new RuntimeException("Permission group not found"));
        Permission permission = permissionRepository.findById(request.permissionId())
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        group.getPermissions().add(permission);
        permissionGroupRepository.saveAndFlush(group);
        return map(group);
    }

    @Transactional(readOnly = true)
    public List<PermissionGroupResponse> getAll() {
        return permissionGroupRepository.findAll().stream().map(this::map).toList();
    }

    @Transactional(readOnly = true)
    public PermissionGroupResponse getById(UUID id) {
        PermissionGroup group = permissionGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission group not found"));
        return map(group);
    }

    @Transactional
    public PermissionGroupResponse update(UUID id, UpdatePermissionGroupRequest request) {
        PermissionGroup group = permissionGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission group not found"));
        group.setName(request.name());
        group.setDescription(request.description());
        return map(permissionGroupRepository.save(group));
    }

    @Transactional
    public void delete(UUID id) {
        PermissionGroup group = permissionGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission group not found"));

        // Detach from all users (user_permission_groups)
        List<User> users = userRepository.findAllByPermissionGroups_Id(id);
        for (User u : users) {
            u.getPermissionGroups().removeIf(pg -> pg.getId().equals(id));
        }
        userRepository.saveAll(users);

        // Detach permissions (permission_group_permissions)
        group.getPermissions().clear();
        permissionGroupRepository.saveAndFlush(group);

        permissionGroupRepository.delete(group);
    }

    private PermissionGroupResponse map(PermissionGroup group) {
        return new PermissionGroupResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getPermissions().stream()
                        .map(Permission::getPermissionKey)
                        .collect(Collectors.toSet())
        );
    }
}