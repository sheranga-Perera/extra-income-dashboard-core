package com.phx.ei.ads.controller;

import com.phx.ei.ads.dto.AdRequestCreate;
import com.phx.ei.ads.dto.AdRequestResponse;
import com.phx.ei.ads.entity.AdRequest;
import com.phx.ei.ads.entity.AdStatus;
import com.phx.ei.ads.repository.AdRequestRepository;
import com.phx.ei.admin.entity.AdminUser;
import com.phx.ei.admin.service.CurrentAdminService;
import com.phx.ei.common.security.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdControllerTest {

    @Mock
    private AdRequestRepository adRequestRepository;

    @Mock
    private CurrentAdminService currentAdminService;

    @InjectMocks
    private AdController adController;

    @Test
    void createRequest_ValidRequest_SavesAndReturnsResponse() {
        AdRequestCreate request = new AdRequestCreate();
        request.setCompanyName(" Acme ");
        request.setContactPerson(" Alex ");
        request.setContactEmail(" alex@acme.com ");
        request.setAdTitle(" Title ");
        request.setAdDescription(" Desc ");
        request.setAdType(" Video ");
        request.setViewsPerDay(10);

        when(adRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<AdRequestResponse> response = adController.createRequest(request);

        AdRequestResponse body = response.getBody();
        assertNotNull(body);
        assertNotNull(body.getId());
        assertEquals("Acme", body.getCompanyName());
        assertEquals("Alex", body.getContactPerson());
        assertEquals("alex@acme.com", body.getContactEmail());
        assertEquals("Title", body.getAdTitle());
        assertEquals("Desc", body.getAdDescription());
        assertEquals("Video", body.getAdType());
        assertEquals(AdStatus.PENDING, body.getStatus());
    }

    @Test
    void createRequest_MissingRequiredFields_Throws() {
        AdRequestCreate request = new AdRequestCreate();
        request.setCompanyName("Acme");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> adController.createRequest(request));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void listRequests_WithStatus_UsesStatusQuery() {
        when(currentAdminService.getCurrentAdmin())
                .thenReturn(new AdminUser(UUID.randomUUID(), "admin", "pw", Role.ADMIN));
        when(adRequestRepository.findByStatusOrderByCreatedAtDesc(AdStatus.PENDING)).thenReturn(List.of());

        ResponseEntity<List<AdRequestResponse>> response = adController.listRequests(AdStatus.PENDING);

        assertNotNull(response.getBody());
        verify(adRequestRepository).findByStatusOrderByCreatedAtDesc(AdStatus.PENDING);
        verify(adRequestRepository, never()).findAll();
    }

    @Test
    void approveRequest_AlreadyApproved_ReturnsUnchanged() {
        when(currentAdminService.getCurrentAdmin())
                .thenReturn(new AdminUser(UUID.randomUUID(), "admin", "pw", Role.ADMIN));

        AdRequest adRequest = new AdRequest();
        adRequest.setId(UUID.randomUUID());
        adRequest.setCompanyName("Acme");
        adRequest.setContactPerson("Alex");
        adRequest.setContactEmail("alex@acme.com");
        adRequest.setAdTitle("Title");
        adRequest.setAdDescription("Desc");
        adRequest.setAdType("Video");
        adRequest.setStatus(AdStatus.APPROVED);

        when(adRequestRepository.findById(adRequest.getId())).thenReturn(Optional.of(adRequest));

        ResponseEntity<AdRequestResponse> response = adController.approveRequest(adRequest.getId(), null);

        assertEquals(AdStatus.APPROVED, response.getBody().getStatus());
        verify(adRequestRepository, never()).save(any());
    }
}
