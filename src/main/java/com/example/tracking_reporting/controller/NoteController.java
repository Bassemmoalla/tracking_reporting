package com.example.tracking_reporting.controller;

import com.example.tracking_reporting.dto.NoteRequest;
import com.example.tracking_reporting.dto.NoteResponse;
import com.example.tracking_reporting.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<NoteResponse> getAll() {
        return noteService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public NoteResponse create(@Valid @RequestBody NoteRequest request) {
        return noteService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public NoteResponse update(@PathVariable UUID id, @Valid @RequestBody NoteRequest request) {
        return noteService.update(id, request);
    }
}