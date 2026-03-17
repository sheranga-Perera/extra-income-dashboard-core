package com.phx.ei.admin.service;

import com.phx.ei.admin.dto.LoginRequest;
import com.phx.ei.admin.dto.RegisterRequest;
import com.phx.ei.admin.entity.AdminUser;
import com.phx.ei.admin.repository.AdminUserRepository;
import com.phx.ei.common.security.JwtUtils;
import com.phx.ei.common.security.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public String register(RegisterRequest request) {
        if (request == null
                || request.getUsername() == null || request.getUsername().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()
                || request.getConfirmPassword() == null || request.getConfirmPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing registration fields");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }

        if (adminUserRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        AdminUser adminUser = new AdminUser(
                UUID.randomUUID(),
                request.getUsername().trim(),
                passwordEncoder.encode(request.getPassword()),
                Role.ADMIN
        );
        adminUserRepository.save(adminUser);
        log.info("Admin registered: adminId={}, username={}", adminUser.getId(), adminUser.getUsername());
        return jwtUtils.generateToken(adminUser.getUsername());
    }

    @Override
    public String login(LoginRequest request) {
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing login fields");
        }
        AdminUser adminUser = adminUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), adminUser.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        log.info("Admin login: adminId={}, username={}", adminUser.getId(), adminUser.getUsername());
        return jwtUtils.generateToken(adminUser.getUsername());
    }
}
