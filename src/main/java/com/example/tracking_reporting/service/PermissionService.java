package com.example.tracking_reporting.service;

import com.example.tracking_reporting.dto.CreatePermissionRequest;
import com.example.tracking_reporting.dto.PermissionResponse;
import com.example.tracking_reporting.dto.UpdatePermissionRequest;
import com.example.tracking_reporting.entity.Permission;
import com.example.tracking_reporting.entity.PermissionGroup;
import com.example.tracking_reporting.repository.PermissionGroupRepository;
import com.example.tracking_reporting.repository.PermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionGroupRepository permissionGroupRepository;
    private static final String PERMISSION_NOT_FOUND = "Permission not found";


    public PermissionService(PermissionRepository permissionRepository,
                             PermissionGroupRepository permissionGroupRepository) {
        this.permissionRepository = permissionRepository;
        this.permissionGroupRepository = permissionGroupRepository;
    }

    public PermissionResponse create(CreatePermissionRequest request) {
        Permission p = new Permission();
        p.setPermissionKey(request.permissionKey());
        p.setName(request.name());
        p.setDescription(request.description());
        p.setModule(request.module());
        return map(permissionRepository.save(p));
    }

    @Transactional(readOnly = true)
    public List<PermissionResponse> getAll() {
        return permissionRepository.findAll().stream().map(this::map).toList();
    }

    @Transactional(readOnly = true)
    public PermissionResponse getById(UUID id) {
        Permission p = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(PERMISSION_NOT_FOUND));

        return map(p);
    }

    @Transactional
    public PermissionResponse update(UUID id, UpdatePermissionRequest request) {
        Permission p = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(PERMISSION_NOT_FOUND));


        p.setPermissionKey(request.permissionKey());
        p.setName(request.name());
        p.setDescription(request.description());
        p.setModule(request.module());

        return map(permissionRepository.save(p));
    }

    @Transactional
    public void delete(UUID id) {
        Permission p = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(PERMISSION_NOT_FOUND));


        // Detach from all groups to avoid FK constraint errors on join table
        List<PermissionGroup> groups = permissionGroupRepository.findAllByPermissions_Id(id);
        for (PermissionGroup g : groups) {
            g.getPermissions().removeIf(per -> per.getId().equals(id));
        }
        permissionGroupRepository.saveAll(groups);

        permissionRepository.delete(p);
    }

    private PermissionResponse map(Permission p) {
        return new PermissionResponse(
                p.getId(),
                p.getPermissionKey(),
                p.getName(),
                p.getDescription(),
                p.getModule()
        );
    }
}