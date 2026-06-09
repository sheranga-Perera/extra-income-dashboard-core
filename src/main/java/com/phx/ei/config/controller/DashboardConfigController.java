package com.phx.ei.config.controller;

import com.phx.ei.admin.service.CurrentAdminService;
import com.phx.ei.config.dto.DashboardSettingRequest;
import com.phx.ei.config.dto.DashboardSettingResponse;
import com.phx.ei.config.entity.DashboardSetting;
import com.phx.ei.config.repository.DashboardSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
@Slf4j
public class DashboardConfigController {

    private final DashboardSettingRepository dashboardSettingRepository;
    private final CurrentAdminService currentAdminService;

    @GetMapping
    public ResponseEntity<List<DashboardSettingResponse>> listSettings() {
        requireAdmin();
        return ResponseEntity.ok(dashboardSettingRepository.findAll().stream()
                .map(this::toResponse)
                .toList());
    }

    @PutMapping("/{key}")
    public ResponseEntity<DashboardSettingResponse> saveSetting(
            @PathVariable String key,
            @RequestBody DashboardSettingRequest request
    ) {
        requireAdmin();
        String normalizedKey = normalizeKey(key);
        DashboardSetting setting = dashboardSettingRepository.findById(normalizedKey)
                .orElseGet(() -> {
                    DashboardSetting created = new DashboardSetting();
                    created.setSettingKey(normalizedKey);
                    return created;
                });

        setting.setSettingValue(request == null ? null : trimOrNull(request.getValue()));
        setting.setDescription(request == null ? null : trimOrNull(request.getDescription()));
        DashboardSetting saved = dashboardSettingRepository.save(setting);
        log.info("Dashboard setting saved: key={}", saved.getSettingKey());
        return ResponseEntity.ok(toResponse(saved));
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteSetting(@PathVariable String key) {
        requireAdmin();
        String normalizedKey = normalizeKey(key);
        if (!dashboardSettingRepository.existsById(normalizedKey)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Setting not found");
        }
        dashboardSettingRepository.deleteById(normalizedKey);
        log.info("Dashboard setting deleted: key={}", normalizedKey);
        return ResponseEntity.noContent().build();
    }

    private void requireAdmin() {
        currentAdminService.getCurrentAdmin();
    }

    private DashboardSettingResponse toResponse(DashboardSetting setting) {
        return new DashboardSettingResponse(
                setting.getSettingKey(),
                setting.getSettingValue(),
                setting.getDescription(),
                setting.getUpdatedAt()
        );
    }

    private String normalizeKey(String key) {
        if (key == null || key.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Setting key is required");
        }
        return key.trim().toLowerCase();
    }

    private String trimOrNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
