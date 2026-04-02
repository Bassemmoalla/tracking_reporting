package com.example.tracking_reporting.repository;

import com.example.tracking_reporting.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {
}