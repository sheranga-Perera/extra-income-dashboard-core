package com.phx.ei.admin.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void testLoginRequest_Creation() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
        
        assertEquals("testuser", loginRequest.getUsername());
        assertEquals("password123", loginRequest.getPassword());
    }

    @Test
    void testLoginRequest_DefaultValues() {
        LoginRequest loginRequest = new LoginRequest();
        
        assertNull(loginRequest.getUsername());
        assertNull(loginRequest.getPassword());
    }

    @Test
    void testLoginRequest_EmptyValues() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("");
        loginRequest.setPassword("");
        
        assertEquals("", loginRequest.getUsername());
        assertEquals("", loginRequest.getPassword());
    }

    @Test
    void testLoginRequest_NullValues() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(null);
        loginRequest.setPassword(null);
        
        assertNull(loginRequest.getUsername());
        assertNull(loginRequest.getPassword());
    }
} 
