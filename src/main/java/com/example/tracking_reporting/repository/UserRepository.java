package com.example.tracking_reporting.repository;

import com.example.tracking_reporting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    boolean existsByCin(String cin);

    Optional<User> findByKeycloakId(String keycloakId);

    Optional<User> findByEmail(String email);

    List<User> findAllByPermissionGroups_Id(UUID permissionGroupId);
}