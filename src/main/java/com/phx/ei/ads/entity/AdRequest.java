package com.phx.ei.ads.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ad_requests")
@SQLDelete(sql = "UPDATE ad_requests SET deleted = 1 WHERE id = ?")
@SQLRestriction("deleted = 0")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdRequest {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String contactPerson;

    @Column(nullable = false)
    private String contactEmail;

    private String contactPhone;

    @Column(nullable = false)
    private String adTitle;

    @Column(nullable = false)
    private String adDescription;

    @Column(nullable = false)
    private String adType;

    private String adGoal;

    @Column(columnDefinition = "TEXT")
    private String mediaUrl;

    @Column(columnDefinition = "TEXT")
    private String mediaContent;

    @Column(columnDefinition = "TEXT")
    private String mediaNotes;

    private String cta;

    @Column(columnDefinition = "TEXT")
    private String ctaUrl;

    private Integer viewsPerDay;

    private Integer minutesPerDay;

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime approvedAt;

    private LocalDateTime rejectedAt;

    @Column(nullable = false)
    private Integer deleted = 0;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) {
            status = AdStatus.PENDING;
        }
        if (deleted == null) {
            deleted = 0;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
