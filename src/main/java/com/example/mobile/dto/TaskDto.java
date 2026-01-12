package com.example.mobile.dto;

import com.example.mobile.entity.TaskStatus;
import com.example.mobile.entity.TaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private Integer points;
    private TaskStatus status;
    private TaskType type;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
