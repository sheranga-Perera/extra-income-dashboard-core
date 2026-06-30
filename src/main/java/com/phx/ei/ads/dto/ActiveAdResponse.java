package com.phx.ei.ads.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ActiveAdResponse {
    private UUID id;
    private String companyName;
    private String adTitle;
    private String adDescription;
    private String adType;
    private String mediaUrl;
    private String mediaContent;
    private String cta;
    private String ctaUrl;
    private Integer viewsPerDay;
    private Integer minutesPerDay;
    private LocalDate startDate;
    private LocalDate endDate;
}
