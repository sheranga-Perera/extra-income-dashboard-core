package com.phx.ei.admin.controller;

import com.phx.ei.admin.dto.AdminUserCreateRequest;
import com.phx.ei.admin.dto.AdminUserResponse;
import com.phx.ei.admin.entity.AdminUser;
import com.phx.ei.admin.repository.AdminUserRepository;
import com.phx.ei.admin.service.CurrentAdminService;
import com.phx.ei.common.security.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final AdminUserRepository adminUserRepository;
    private final CurrentAdminService currentAdminService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<List<AdminUserResponse>> listAdmins() {
        requireAdmin();
        return ResponseEntity.ok(adminUserRepository.findAll().stream()
                .map(this::toResponse)
                .toList());
    }

    @PostMapping
    public ResponseEntity<AdminUserResponse> createAdmin(@RequestBody AdminUserCreateRequest request) {
        requireAdmin();
        if (request == null || isBlank(request.getUsername()) || isBlank(request.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing admin user fields");
        }

        String username = request.getUsername().trim();
        if (adminUserRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        AdminUser admin = new AdminUser(
                UUID.randomUUID(),
                username,
                passwordEncoder.encode(request.getPassword()),
                Role.ADMIN
        );
        AdminUser saved = adminUserRepository.save(admin);
        log.info("Admin user created: adminId={}, username={}", saved.getId(), saved.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable UUID id) {
        AdminUser currentAdmin = requireAdmin();
        if (currentAdmin.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete the current admin");
        }
        if (!adminUserRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin user not found");
        }

        adminUserRepository.deleteById(id);
        log.info("Admin user deleted: adminId={}, deletedBy={}", id, currentAdmin.getId());
        return ResponseEntity.noContent().build();
    }

    private AdminUser requireAdmin() {
        return currentAdminService.getCurrentAdmin();
    }

    private AdminUserResponse toResponse(AdminUser admin) {
        return new AdminUserResponse(admin.getId(), admin.getUsername(), admin.getRole());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
