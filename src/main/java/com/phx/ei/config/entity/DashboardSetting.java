package com.phx.ei.config.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "dashboard_settings")
@SQLDelete(sql = "UPDATE dashboard_settings SET deleted = 1 WHERE setting_key = ?")
@SQLRestriction("deleted = 0")
@Data
@NoArgsConstructor
public class DashboardSetting {

    @Id
    @Column(nullable = false, length = 120)
    private String settingKey;

    @Column(columnDefinition = "TEXT")
    private String settingValue;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Integer deleted = 0;

    @PrePersist
    @PreUpdate
    public void onSave() {
        updatedAt = LocalDateTime.now();
        if (deleted == null) {
            deleted = 0;
        }
    }
}
