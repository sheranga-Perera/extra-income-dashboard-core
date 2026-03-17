package com.phx.ei.common.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import org.junit.jupiter.api.BeforeEach;

import java.util.UUID;
import com.phx.ei.admin.entity.AdminUser;
import com.phx.ei.admin.repository.AdminUserRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SecurityConfigTest {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        adminUserRepository.deleteAll();
        AdminUser user = new AdminUser(
                UUID.randomUUID(),
                "testadmin",
                passwordEncoder.encode("password"),
                Role.ADMIN
        );
        adminUserRepository.save(user);
    }

    @Test
    void testAuthenticationManager_ValidCredentials() {
        String username = "testadmin";
        String rawPassword = "password";
        
        UsernamePasswordAuthenticationToken authRequest = 
            new UsernamePasswordAuthenticationToken(username, rawPassword);
        
        Authentication authentication = authenticationManager.authenticate(authRequest);
        
        assertNotNull(authentication);
        assertTrue(authentication.isAuthenticated());
        assertEquals(username, authentication.getName());
    }

    @Test
    void testAuthenticationManager_InvalidCredentials() {
        UsernamePasswordAuthenticationToken authRequest = 
            new UsernamePasswordAuthenticationToken("testadmin", "wrongpassword");
        
        assertThrows(Exception.class, () -> {
            authenticationManager.authenticate(authRequest);
        });
    }

    @Test
    void testAuthenticationManager_NonExistentUser() {
        UsernamePasswordAuthenticationToken authRequest = 
            new UsernamePasswordAuthenticationToken("nonexistentuser", "password");
        
        assertThrows(Exception.class, () -> {
            authenticationManager.authenticate(authRequest);
        });
    }

    @Test
    void testSecurityConfiguration_BeansCreated() {
        // This test verifies that the security configuration beans are created successfully
        assertNotNull(authenticationManager);
    }
} 
