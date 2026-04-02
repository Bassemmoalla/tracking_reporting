package com.example.tracking_reporting.repository;

import com.example.tracking_reporting.entity.TeamAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamAssignmentRepository extends JpaRepository<TeamAssignment, UUID> {
    Optional<TeamAssignment> findByTeam_IdAndUser_Id(UUID teamId, UUID userId);
    List<TeamAssignment> findAllByTeam_Id(UUID teamId);
    void deleteAllByTeam_Id(UUID teamId);
    void deleteAllByUser_Id(UUID userId);
}