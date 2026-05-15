package com.example.tracking_reporting.service;

import com.example.tracking_reporting.dto.CommentRequest;
import com.example.tracking_reporting.dto.CommentResponse;
import com.example.tracking_reporting.entity.Comment;
import com.example.tracking_reporting.entity.Project;
import com.example.tracking_reporting.entity.Task;
import com.example.tracking_reporting.exception.ResourceNotFoundException;
import com.example.tracking_reporting.helper.EntityFinder;
import com.example.tracking_reporting.repository.CommentRepository;
import com.example.tracking_reporting.repository.TaskRepository;
import com.example.tracking_reporting.security.CurrentUsernameProvider;
import com.example.tracking_reporting.service.report.AuditLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CommentService {

    private static final String COMMENT_NOT_FOUND = "Comment not found with id: ";
    private static final String TASK_NOT_FOUND = "Task not found with id: ";

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final EntityFinder entityFinder;
    private final CurrentUsernameProvider currentUsernameProvider;
    private final AuditLogService auditLogService;

    public CommentService(CommentRepository commentRepository,
                          TaskRepository taskRepository,
                          EntityFinder entityFinder,
                          CurrentUsernameProvider currentUsernameProvider,
                          AuditLogService auditLogService) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.entityFinder = entityFinder;
        this.currentUsernameProvider = currentUsernameProvider;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getAll() {
        return commentRepository.findAllByOrderByUpdatedAtDesc()
                .stream()
                .map(this::map)
                .toList();
    }

    public CommentResponse create(CommentRequest request) {
        Project project = resolveProject(request.projectId());
        Task task = resolveTask(request.taskId());

        validateProjectTaskConsistency(project, task);

        Comment comment = Comment.builder()
                .title(request.title().trim())
                .content(request.content().trim())
                .project(project)
                .task(task)
                .createdBy(currentUsernameProvider.getCurrentUsername())
                .build();

        Comment saved = commentRepository.save(comment);

        auditLogService.save(
                "CREATE_COMMENT",
                currentUsernameProvider.getCurrentUsername(),
                "Comment",
                saved.getId().toString(),
                "created comment"
        );

        return map(saved);
    }

    public CommentResponse update(UUID id, CommentRequest request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(COMMENT_NOT_FOUND + id));

        Project project = resolveProject(request.projectId());
        Task task = resolveTask(request.taskId());

        validateProjectTaskConsistency(project, task);

        comment.setTitle(request.title().trim());
        comment.setContent(request.content().trim());
        comment.setProject(project);
        comment.setTask(task);

        Comment saved = commentRepository.save(comment);

        auditLogService.save(
                "UPDATE_COMMENT",
                currentUsernameProvider.getCurrentUsername(),
                "Comment",
                saved.getId().toString(),
                "updated comment"
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

    private CommentResponse map(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getTitle(),
                comment.getContent(),
                comment.getProject() != null ? comment.getProject().getId() : null,
                comment.getProject() != null ? comment.getProject().getName() : null,
                comment.getTask() != null ? comment.getTask().getId() : null,
                comment.getTask() != null ? comment.getTask().getName() : null,
                comment.getCreatedBy(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}