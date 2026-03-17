package com.phx.ei.admin.entity;

import com.phx.ei.common.security.Role;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AdminUserTest {

    @Test
    void testAdminUser_Creation() {
        UUID id = UUID.randomUUID();
        AdminUser user = new AdminUser(id, "admin", "encoded", Role.ADMIN);

        assertEquals(id, user.getId());
        assertEquals("admin", user.getUsername());
        assertEquals("encoded", user.getPassword());
        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    void testAdminUser_Setters() {
        AdminUser user = new AdminUser();
        user.setId(UUID.randomUUID());
        user.setUsername("admin2");
        user.setPassword("encoded2");
        user.setRole(Role.ADMIN);

        assertNotNull(user.getId());
        assertEquals("admin2", user.getUsername());
        assertEquals("encoded2", user.getPassword());
        assertEquals(Role.ADMIN, user.getRole());
    }
}
