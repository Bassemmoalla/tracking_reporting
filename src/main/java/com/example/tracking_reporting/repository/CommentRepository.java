package com.example.tracking_reporting.repository;

import com.example.tracking_reporting.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findAllByOrderByUpdatedAtDesc();
}