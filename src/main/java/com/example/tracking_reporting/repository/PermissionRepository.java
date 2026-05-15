package com.example.tracking_reporting.repository;

import com.example.tracking_reporting.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    List<Permission> findByPermissionKeyIn(Collection<String> permissionKeys);
    Optional<Permission> findByPermissionKey(String permissionKey);
    List<Permission> findByPermissionKeyIn(Set<String> permissionKeys);
}