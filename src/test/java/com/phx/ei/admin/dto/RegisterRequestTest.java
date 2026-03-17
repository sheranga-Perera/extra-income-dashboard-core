package com.phx.ei.admin.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {

    @Test
    void testRegisterRequest_Creation() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("newpassword123");
        registerRequest.setConfirmPassword("newpassword123");
        
        assertEquals("newuser", registerRequest.getUsername());
        assertEquals("newpassword123", registerRequest.getPassword());
        assertEquals("newpassword123", registerRequest.getConfirmPassword());
    }

    @Test
    void testRegisterRequest_DefaultValues() {
        RegisterRequest registerRequest = new RegisterRequest();
        
        assertNull(registerRequest.getUsername());
        assertNull(registerRequest.getPassword());
        assertNull(registerRequest.getConfirmPassword());
    }

    @Test
    void testRegisterRequest_EmptyValues() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("");
        registerRequest.setPassword("");
        registerRequest.setConfirmPassword("");
        
        assertEquals("", registerRequest.getUsername());
        assertEquals("", registerRequest.getPassword());
        assertEquals("", registerRequest.getConfirmPassword());
    }

    @Test
    void testRegisterRequest_NullValues() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(null);
        registerRequest.setPassword(null);
        registerRequest.setConfirmPassword(null);
        
        assertNull(registerRequest.getUsername());
        assertNull(registerRequest.getPassword());
        assertNull(registerRequest.getConfirmPassword());
    }
} 
