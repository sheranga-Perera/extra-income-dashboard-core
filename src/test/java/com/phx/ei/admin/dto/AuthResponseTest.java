package com.phx.ei.admin.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthResponseTest {

    @Test
    void testAuthResponse_Creation() {
        AuthResponse authResponse = new AuthResponse("jwt.token.here");
        
        assertEquals("jwt.token.here", authResponse.getToken());
    }

    @Test
    void testAuthResponse_EmptyToken() {
        AuthResponse authResponse = new AuthResponse("");
        
        assertEquals("", authResponse.getToken());
    }

    @Test
    void testAuthResponse_NullToken() {
        AuthResponse authResponse = new AuthResponse(null);
        
        assertNull(authResponse.getToken());
    }
} 
