package com.phx.ei.ads.dto;

import lombok.Data;

@Data
public class AdRequestCreate {
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
    private Integer viewsPerDay;
    private Integer minutesPerDay;
    private String startDate;
    private String endDate;
}
