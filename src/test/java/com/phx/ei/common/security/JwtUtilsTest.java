package com.phx.ei.common.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "jwt.secret=testjwtsecretkey123456789",
    "jwt.expirationMs=3600000"
})
class JwtUtilsTest {

    @Autowired
    private JwtUtils jwtUtils;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(new ArrayList<>())
                .build();
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtils.generateToken("testuser");
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void testExtractUsername() {
        String username = "testuser";
        String token = jwtUtils.generateToken(username);
        
        String extractedUsername = jwtUtils.extractUsername(token);
        
        assertEquals(username, extractedUsername);
    }

    @Test
    void testValidateToken_ValidToken() {
        String token = jwtUtils.generateToken("testuser");
        
        boolean isValid = jwtUtils.validateToken(token, userDetails);
        
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidUsername() {
        String token = jwtUtils.generateToken("testuser");
        UserDetails wrongUser = User.builder()
                .username("wronguser")
                .password("password")
                .authorities(new ArrayList<>())
                .build();
        
        boolean isValid = jwtUtils.validateToken(token, wrongUser);
        
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_NullToken() {
        assertThrows(Exception.class, () -> {
            jwtUtils.validateToken(null, userDetails);
        });
    }

    @Test
    void testValidateToken_EmptyToken() {
        assertThrows(Exception.class, () -> {
            jwtUtils.validateToken("", userDetails);
        });
    }

    @Test
    void testValidateToken_InvalidToken() {
        assertThrows(Exception.class, () -> {
            jwtUtils.validateToken("invalid.token.here", userDetails);
        });
    }
} 
