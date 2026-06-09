package com.phx.ei.config.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DashboardSettingResponse {
    private String key;
    private String value;
    private String description;
    private LocalDateTime updatedAt;
}
