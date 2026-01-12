package com.example.mobile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserLeaderboardDto {
    private Long id;
    private String username;
    private Integer points;
}
