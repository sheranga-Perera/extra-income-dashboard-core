package com.phx.ei.common.security;

import com.phx.ei.admin.entity.AdminUser;
import com.phx.ei.admin.repository.AdminUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JwtFilterTest {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AdminUserRepository adminUserRepository;

    private AdminUser testUser;
    private String validToken;

    @BeforeEach
    void setUp() {
        // Clear security context
        SecurityContextHolder.clearContext();
        
        // Create a test user
        testUser = new AdminUser(
                UUID.randomUUID(),
                "testadmin",
                "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG",
                Role.ADMIN
        );
        adminUserRepository.save(testUser);
        
        // Generate a valid token
        validToken = jwtUtils.generateToken("testadmin");
    }

    @Test
    void testDoFilterInternal_ValidToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        
        request.addHeader("Authorization", "Bearer " + validToken);
        
        jwtFilter.doFilterInternal(request, response, filterChain);
        
        // Verify that authentication was set
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("testadmin", SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void testDoFilterInternal_NoAuthorizationHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        
        jwtFilter.doFilterInternal(request, response, filterChain);
        
        // Verify that no authentication was set
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_InvalidAuthorizationHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        
        request.addHeader("Authorization", "InvalidFormat token");
        
        jwtFilter.doFilterInternal(request, response, filterChain);
        
        // Verify that no authentication was set
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_InvalidToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        
        request.addHeader("Authorization", "Bearer invalid.token.here");
        
        jwtFilter.doFilterInternal(request, response, filterChain);
        
        // Verify that no authentication was set
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_UserNotInDatabase() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        
        // Generate token for user that doesn't exist in database
        String tokenForNonExistentUser = jwtUtils.generateToken("nonexistentuser");
        request.addHeader("Authorization", "Bearer " + tokenForNonExistentUser);
        
        jwtFilter.doFilterInternal(request, response, filterChain);
        
        // Verify that no authentication was set
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_AlreadyAuthenticated() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        
        request.addHeader("Authorization", "Bearer " + validToken);
        
        // Set authentication first
        jwtFilter.doFilterInternal(request, response, filterChain);
        String firstAuthName = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Call filter again
        MockFilterChain secondChain = new MockFilterChain();
        jwtFilter.doFilterInternal(request, response, secondChain);
        String secondAuthName = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Verify authentication remains the same
        assertEquals(firstAuthName, secondAuthName);
    }
} 
