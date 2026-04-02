package com.example.tracking_reporting.mapper;

import com.example.tracking_reporting.dto.IterationResponse;
import com.example.tracking_reporting.entity.Iteration;
import org.springframework.stereotype.Component;

@Component
public class IterationMapper {

    public IterationResponse toResponse(Iteration iteration) {
        return new IterationResponse(
                iteration.getId(),
                iteration.getName(),
                iteration.getPlannedBudget(),
                iteration.getRealBudget(),
                iteration.getEstimatedFinishDate(),
                iteration.getRealFinishDate(),
                iteration.getObjective(),
                iteration.getStatus(),
                iteration.getProject().getId(),
                iteration.getProject().getName(),
                iteration.getCreatedAt(),
                iteration.getUpdatedAt()
        );
    }
}