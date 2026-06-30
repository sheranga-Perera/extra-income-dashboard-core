package com.phx.ei.ads.dto;

import com.phx.ei.ads.entity.AdStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AdRequestResponse {
    private UUID id;
    private String companyName;
    private String contactPerson;
    private String contactEmail;
    private String contactPhone;
    private String adTitle;
    private String adDescription;
    private String adType;
    private String adGoal;
    private String mediaUrl;
    private String mediaContent;
    private String mediaNotes;
    private String cta;
    private String ctaUrl;
    private Integer viewsPerDay;
    private Integer minutesPerDay;
    private LocalDate startDate;
    private LocalDate endDate;
    private AdStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;
}
