package com.phx.ei.ads.controller;

import com.phx.ei.ads.dto.ActiveAdResponse;
import com.phx.ei.ads.dto.AdApprovalRequest;
import com.phx.ei.ads.dto.AdRequestCreate;
import com.phx.ei.ads.dto.AdRequestResponse;
import com.phx.ei.ads.dto.AdSummaryResponse;
import com.phx.ei.ads.entity.AdRequest;
import com.phx.ei.ads.entity.AdStatus;
import com.phx.ei.ads.repository.AdRequestRepository;
import com.phx.ei.admin.service.CurrentAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
@Slf4j
public class AdController {

    private final AdRequestRepository adRequestRepository;
    private final CurrentAdminService currentAdminService;

    @PostMapping("/requests")
    public ResponseEntity<AdRequestResponse> createRequest(@RequestBody AdRequestCreate request) {
        validateRequest(request);

        AdRequest adRequest = new AdRequest();
        adRequest.setId(UUID.randomUUID());
        adRequest.setCompanyName(request.getCompanyName().trim());
        adRequest.setContactPerson(request.getContactPerson().trim());
        adRequest.setContactEmail(request.getContactEmail().trim());
        adRequest.setContactPhone(trimOrNull(request.getContactPhone()));
        adRequest.setAdTitle(request.getAdTitle().trim());
        adRequest.setAdDescription(request.getAdDescription().trim());
        adRequest.setAdType(request.getAdType().trim());
        adRequest.setAdGoal(trimOrNull(request.getAdGoal()));
        adRequest.setMediaUrl(trimOrNull(request.getMediaUrl()));
        adRequest.setMediaContent(trimOrNull(request.getMediaContent()));
        adRequest.setMediaNotes(trimOrNull(request.getMediaNotes()));
        adRequest.setCta(trimOrNull(request.getCta()));
        adRequest.setViewsPerDay(request.getViewsPerDay());
        adRequest.setMinutesPerDay(request.getMinutesPerDay());
        adRequest.setStartDate(parseDate(request.getStartDate(), "startDate"));
        adRequest.setEndDate(parseDate(request.getEndDate(), "endDate"));
        adRequest.setStatus(AdStatus.PENDING);

        AdRequest saved = adRequestRepository.save(adRequest);
        log.info("Ad request created: requestId={}, companyName={}", saved.getId(), saved.getCompanyName());
        return ResponseEntity.ok(toResponse(saved));
    }

    @GetMapping("/requests")
    public ResponseEntity<List<AdRequestResponse>> listRequests(@RequestParam(value = "status", required = false) AdStatus status) {
        requireAdmin();
        List<AdRequest> requests = status == null
                ? adRequestRepository.findAll()
                : adRequestRepository.findByStatusOrderByCreatedAtDesc(status);
        return ResponseEntity.ok(requests.stream().map(this::toResponse).toList());
    }

    @PatchMapping("/requests/{id}/approve")
    public ResponseEntity<AdRequestResponse> approveRequest(
            @PathVariable UUID id,
            @RequestBody(required = false) AdApprovalRequest request
    ) {
        requireAdmin();
        AdRequest adRequest = adRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ad request not found"));

        if (adRequest.getStatus() == AdStatus.APPROVED) {
            return ResponseEntity.ok(toResponse(adRequest));
        }

        LocalDate startDate = request != null && request.getStartDate() != null
                ? parseDate(request.getStartDate(), "startDate")
                : adRequest.getStartDate();
        LocalDate endDate = request != null && request.getEndDate() != null
                ? parseDate(request.getEndDate(), "endDate")
                : adRequest.getEndDate();

        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date must be after start date");
        }

        adRequest.setStartDate(startDate);
        adRequest.setEndDate(endDate);
        adRequest.setStatus(AdStatus.APPROVED);
        adRequest.setApprovedAt(LocalDateTime.now());
        adRequest.setRejectedAt(null);

        AdRequest saved = adRequestRepository.save(adRequest);
        log.info("Ad request approved: requestId={}, adminUserId={}", saved.getId(), currentAdminService.getCurrentAdmin().getId());
        return ResponseEntity.ok(toResponse(saved));
    }

    @PatchMapping("/requests/{id}/reject")
    public ResponseEntity<AdRequestResponse> rejectRequest(@PathVariable UUID id) {
        requireAdmin();
        AdRequest adRequest = adRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ad request not found"));

        adRequest.setStatus(AdStatus.REJECTED);
        adRequest.setRejectedAt(LocalDateTime.now());
        adRequest.setApprovedAt(null);

        AdRequest saved = adRequestRepository.save(adRequest);
        log.info("Ad request rejected: requestId={}, adminUserId={}", saved.getId(), currentAdminService.getCurrentAdmin().getId());
        return ResponseEntity.ok(toResponse(saved));
    }

    @GetMapping("/active")
    public ResponseEntity<List<ActiveAdResponse>> getActiveAds() {
        LocalDate today = LocalDate.now();
        List<ActiveAdResponse> response = adRequestRepository.findActiveAds(AdStatus.APPROVED, today).stream()
                .map(this::toActiveResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary")
    public ResponseEntity<AdSummaryResponse> getSummary() {
        requireAdmin();
        LocalDate today = LocalDate.now();
        return ResponseEntity.ok(new AdSummaryResponse(
                adRequestRepository.countByStatus(AdStatus.PENDING),
                adRequestRepository.countByStatus(AdStatus.APPROVED),
                adRequestRepository.countByStatus(AdStatus.REJECTED),
                adRequestRepository.findActiveAds(AdStatus.APPROVED, today).size()
        ));
    }

    private void validateRequest(AdRequestCreate request) {
        if (request == null
                || isBlank(request.getCompanyName())
                || isBlank(request.getContactPerson())
                || isBlank(request.getContactEmail())
                || isBlank(request.getAdTitle())
                || isBlank(request.getAdDescription())
                || isBlank(request.getAdType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing ad request fields");
        }

        if ((request.getViewsPerDay() == null || request.getViewsPerDay() <= 0)
                && (request.getMinutesPerDay() == null || request.getMinutesPerDay() <= 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide views per day or minutes per day");
        }

        LocalDate startDate = parseDate(request.getStartDate(), "startDate");
        LocalDate endDate = parseDate(request.getEndDate(), "endDate");
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date must be after start date");
        }
    }

    private LocalDate parseDate(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid " + fieldName);
        }
    }

    private void requireAdmin() {
        currentAdminService.getCurrentAdmin();
    }

    private AdRequestResponse toResponse(AdRequest request) {
        return new AdRequestResponse(
                request.getId(),
                request.getCompanyName(),
                request.getContactPerson(),
                request.getContactEmail(),
                request.getContactPhone(),
                request.getAdTitle(),
                request.getAdDescription(),
                request.getAdType(),
                request.getAdGoal(),
                request.getMediaUrl(),
                request.getMediaContent(),
                request.getMediaNotes(),
                request.getCta(),
                request.getViewsPerDay(),
                request.getMinutesPerDay(),
                request.getStartDate(),
                request.getEndDate(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getApprovedAt(),
                request.getRejectedAt()
        );
    }

    private ActiveAdResponse toActiveResponse(AdRequest request) {
        return new ActiveAdResponse(
                request.getId(),
                request.getCompanyName(),
                request.getAdTitle(),
                request.getAdDescription(),
                request.getAdType(),
                request.getMediaUrl(),
                request.getMediaContent(),
                request.getCta(),
                request.getViewsPerDay(),
                request.getMinutesPerDay(),
                request.getStartDate(),
                request.getEndDate()
        );
    }

    private String trimOrNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
