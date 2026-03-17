package com.phx.ei.admin.service;

import com.phx.ei.admin.entity.AdminUser;
import com.phx.ei.admin.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrentAdminService {

    private final AdminUserRepository adminUserRepository;

    public AdminUser getCurrentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Admin lookup failed: unauthenticated request");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        String username = authentication.getName();
        return adminUserRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Admin lookup failed: username not found: username={}", username);
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found");
                });
    }
}
