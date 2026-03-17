package com.phx.ei.admin.repository;

import com.phx.ei.admin.entity.AdminUser;
import com.phx.ei.common.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminUserRepositoryTest {

    @Autowired
    private AdminUserRepository adminUserRepository;

    private AdminUser testUser;

    @BeforeEach
    void setUp() {
        testUser = new AdminUser(
                UUID.randomUUID(),
                "testadmin",
                "encodedpassword",
                Role.ADMIN
        );
    }

    @Test
    void testSaveUser() {
        AdminUser savedUser = adminUserRepository.save(testUser);

        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals("testadmin", savedUser.getUsername());
        assertEquals("encodedpassword", savedUser.getPassword());
        assertEquals(Role.ADMIN, savedUser.getRole());
    }

    @Test
    void testFindByUsername_UserExists() {
        adminUserRepository.save(testUser);

        Optional<AdminUser> foundUser = adminUserRepository.findByUsername("testadmin");

        assertTrue(foundUser.isPresent());
        assertEquals("testadmin", foundUser.get().getUsername());
    }

    @Test
    void testFindByUsername_UserNotExists() {
        Optional<AdminUser> foundUser = adminUserRepository.findByUsername("nonexistentuser");

        assertFalse(foundUser.isPresent());
    }

    @Test
    void testFindByUsername_NullUsername() {
        Optional<AdminUser> foundUser = adminUserRepository.findByUsername(null);

        assertFalse(foundUser.isPresent());
    }

    @Test
    void testFindByUsername_EmptyUsername() {
        Optional<AdminUser> foundUser = adminUserRepository.findByUsername("");

        assertFalse(foundUser.isPresent());
    }

    @Test
    void testFindById_UserExists() {
        AdminUser savedUser = adminUserRepository.save(testUser);

        Optional<AdminUser> foundUser = adminUserRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
    }

    @Test
    void testFindById_UserNotExists() {
        UUID nonExistentId = UUID.randomUUID();
        Optional<AdminUser> foundUser = adminUserRepository.findById(nonExistentId);

        assertFalse(foundUser.isPresent());
    }

    @Test
    void testDeleteUser() {
        AdminUser savedUser = adminUserRepository.save(testUser);

        adminUserRepository.delete(savedUser);

        Optional<AdminUser> foundUser = adminUserRepository.findById(savedUser.getId());
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testUpdateUser() {
        AdminUser savedUser = adminUserRepository.save(testUser);

        savedUser.setPassword("newpassword");
        AdminUser updatedUser = adminUserRepository.save(savedUser);

        assertEquals("newpassword", updatedUser.getPassword());

        Optional<AdminUser> foundUser = adminUserRepository.findById(savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("newpassword", foundUser.get().getPassword());
    }
}
