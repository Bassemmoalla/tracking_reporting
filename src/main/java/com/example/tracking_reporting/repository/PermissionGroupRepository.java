package com.example.tracking_reporting.repository;

import com.example.tracking_reporting.entity.PermissionGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionGroupRepository extends JpaRepository<PermissionGroup, UUID> {
    Optional<PermissionGroup> findByName(String name);
    List<PermissionGroup> findAllByPermissions_Id(UUID permissionId);
}