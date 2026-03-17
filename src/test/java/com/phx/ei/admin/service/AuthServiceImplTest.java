package com.phx.ei.admin.service;

import com.phx.ei.admin.dto.LoginRequest;
import com.phx.ei.admin.dto.RegisterRequest;
import com.phx.ei.admin.entity.AdminUser;
import com.phx.ei.admin.repository.AdminUserRepository;
import com.phx.ei.common.security.JwtUtils;
import com.phx.ei.common.security.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AdminUserRepository adminUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void register_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(" admin ");
        request.setPassword("pass1234");
        request.setConfirmPassword("pass1234");

        when(adminUserRepository.findByUsername(" admin ")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(jwtUtils.generateToken(any())).thenReturn("token");

        String token = authService.register(request);

        assertEquals("token", token);

        ArgumentCaptor<AdminUser> userCaptor = ArgumentCaptor.forClass(AdminUser.class);
        verify(adminUserRepository).save(userCaptor.capture());
        AdminUser saved = userCaptor.getValue();
        assertEquals("admin", saved.getUsername());
        assertEquals("encoded", saved.getPassword());
        assertEquals(Role.ADMIN, saved.getRole());
        assertNotNull(saved.getId());
    }

    @Test
    void register_MissingFields() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("admin");
        request.setPassword(null);
        request.setConfirmPassword("pass1234");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.register(request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void register_PasswordMismatch() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("admin");
        request.setPassword("pass1234");
        request.setConfirmPassword("different");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.register(request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void register_UsernameExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("admin");
        request.setPassword("pass1234");
        request.setConfirmPassword("pass1234");

        when(adminUserRepository.findByUsername("admin"))
                .thenReturn(Optional.of(new AdminUser(UUID.randomUUID(), "admin", "encoded", Role.ADMIN)));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.register(request));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("pass1234");

        AdminUser adminUser = new AdminUser(UUID.randomUUID(), "admin", "encoded", Role.ADMIN);
        when(adminUserRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches("pass1234", "encoded")).thenReturn(true);
        when(jwtUtils.generateToken(any())).thenReturn("token");

        String token = authService.login(request);

        assertEquals("token", token);
        verify(jwtUtils).generateToken("admin");
    }

    @Test
    void login_InvalidPassword() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("wrong");

        AdminUser adminUser = new AdminUser(UUID.randomUUID(), "admin", "encoded", Role.ADMIN);
        when(adminUserRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.login(request));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void login_MissingFields() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword(null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.login(request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }
}
