package com.phx.ei.admin.controller;

import com.phx.ei.admin.dto.AuthResponse;
import com.phx.ei.admin.dto.LoginRequest;
import com.phx.ei.admin.dto.RegisterRequest;
import com.phx.ei.admin.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        log.info("Admin register request received: username={}", request.getUsername());
        String token = authService.register(request);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        log.info("Admin login request received: username={}", request.getUsername());
        String token = authService.login(request);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
