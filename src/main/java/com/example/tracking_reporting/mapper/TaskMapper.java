package com.example.tracking_reporting.mapper;

import com.example.tracking_reporting.dto.TaskResponse;
import com.example.tracking_reporting.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskResponse toResponse(Task task) {
        String assignedUserFullName = null;

        if (task.getAssignedUser() != null) {
            String firstName = task.getAssignedUser().getFirstName() != null
                    ? task.getAssignedUser().getFirstName()
                    : "";

            String lastName = task.getAssignedUser().getLastName() != null
                    ? task.getAssignedUser().getLastName()
                    : "";

            assignedUserFullName = (firstName + " " + lastName).trim();
        }

        return new TaskResponse(
                task.getId(),
                task.getName(),
                task.getShortText(),
                task.getEndTaskDate(),
                task.getDescription(),
                task.getProject().getId(),
                task.getProject().getName(),
                task.getAssignedUser() != null ? task.getAssignedUser().getId() : null,
                assignedUserFullName,
                task.getIteration() != null ? task.getIteration().getId() : null,
                task.getIteration() != null ? task.getIteration().getName() : null,
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}