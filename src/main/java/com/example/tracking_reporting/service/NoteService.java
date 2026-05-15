package com.example.tracking_reporting.service;

import com.example.tracking_reporting.dto.NoteRequest;
import com.example.tracking_reporting.dto.NoteResponse;
import com.example.tracking_reporting.entity.Note;
import com.example.tracking_reporting.entity.Project;
import com.example.tracking_reporting.entity.Task;
import com.example.tracking_reporting.exception.ResourceNotFoundException;
import com.example.tracking_reporting.helper.EntityFinder;
import com.example.tracking_reporting.repository.NoteRepository;
import com.example.tracking_reporting.repository.TaskRepository;
import com.example.tracking_reporting.security.CurrentUsernameProvider;
import com.example.tracking_reporting.service.report.AuditLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NoteService {

    private static final String NOTE_NOT_FOUND = "Note not found with id: ";
    private static final String TASK_NOT_FOUND = "Task not found with id: ";

    private final NoteRepository noteRepository;
    private final TaskRepository taskRepository;
    private final EntityFinder entityFinder;
    private final CurrentUsernameProvider currentUsernameProvider;
    private final AuditLogService auditLogService;

    public NoteService(NoteRepository noteRepository,
                       TaskRepository taskRepository,
                       EntityFinder entityFinder,
                       CurrentUsernameProvider currentUsernameProvider,
                       AuditLogService auditLogService) {
        this.noteRepository = noteRepository;
        this.taskRepository = taskRepository;
        this.entityFinder = entityFinder;
        this.currentUsernameProvider = currentUsernameProvider;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> getAll() {
        return noteRepository.findAllByOrderByUpdatedAtDesc()
                .stream()
                .map(this::map)
                .toList();
    }

    public NoteResponse create(NoteRequest request) {
        Project project = resolveProject(request.projectId());
        Task task = resolveTask(request.taskId());

        validateProjectTaskConsistency(project, task);

        Note note = Note.builder()
                .title(request.title().trim())
                .content(request.content().trim())
                .project(project)
                .task(task)
                .createdBy(currentUsernameProvider.getCurrentUsername())
                .build();

        Note saved = noteRepository.save(note);

        auditLogService.save(
                "CREATE_NOTE",
                currentUsernameProvider.getCurrentUsername(),
                "Note",
                saved.getId().toString(),
                "created note"
        );

        return map(saved);
    }

    public NoteResponse update(UUID id, NoteRequest request) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOTE_NOT_FOUND + id));

        Project project = resolveProject(request.projectId());
        Task task = resolveTask(request.taskId());

        validateProjectTaskConsistency(project, task);

        note.setTitle(request.title().trim());
        note.setContent(request.content().trim());
        note.setProject(project);
        note.setTask(task);

        Note saved = noteRepository.save(note);

        auditLogService.save(
                "UPDATE_NOTE",
                currentUsernameProvider.getCurrentUsername(),
                "Note",
                saved.getId().toString(),
                "updated note"
        );

        return map(saved);
    }

    private Project resolveProject(UUID projectId) {
        if (projectId == null) {
            return null;
        }
        return entityFinder.getProject(projectId);
    }

    private Task resolveTask(UUID taskId) {
        if (taskId == null) {
            return null;
        }

        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND + taskId));
    }

    private void validateProjectTaskConsistency(Project project, Task task) {
        if (project != null && task != null && task.getProject() != null && !project.getId().equals(task.getProject().getId())) {
            throw new IllegalArgumentException("Task does not belong to the selected project");
        }
    }

    private NoteResponse map(Note note) {
        return new NoteResponse(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getProject() != null ? note.getProject().getId() : null,
                note.getProject() != null ? note.getProject().getName() : null,
                note.getTask() != null ? note.getTask().getId() : null,
                note.getTask() != null ? note.getTask().getName() : null,
                note.getCreatedBy(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }
}