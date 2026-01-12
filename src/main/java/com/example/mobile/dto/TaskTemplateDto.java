package com.example.mobile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TaskTemplateDto {
    private Long id;
    private String title;
    private String description;
    private Integer points;
}
