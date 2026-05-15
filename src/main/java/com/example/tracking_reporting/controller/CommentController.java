package com.example.tracking_reporting.controller;

import com.example.tracking_reporting.dto.CommentRequest;
import com.example.tracking_reporting.dto.CommentResponse;
import com.example.tracking_reporting.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<CommentResponse> getAll() {
        return commentService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public CommentResponse create(@Valid @RequestBody CommentRequest request) {
        return commentService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public CommentResponse update(@PathVariable UUID id, @Valid @RequestBody CommentRequest request) {
        return commentService.update(id, request);
    }
}