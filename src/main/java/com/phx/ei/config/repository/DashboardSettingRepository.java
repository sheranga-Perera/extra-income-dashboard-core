package com.phx.ei.config.repository;

import com.phx.ei.config.entity.DashboardSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardSettingRepository extends JpaRepository<DashboardSetting, String> {
}
