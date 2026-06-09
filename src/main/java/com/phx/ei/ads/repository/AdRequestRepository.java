package com.phx.ei.ads.repository;

import com.phx.ei.ads.entity.AdRequest;
import com.phx.ei.ads.entity.AdStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AdRequestRepository extends JpaRepository<AdRequest, UUID> {
    List<AdRequest> findByStatusOrderByCreatedAtDesc(AdStatus status);

    long countByStatus(AdStatus status);

    @Query("""
            SELECT a FROM AdRequest a
            WHERE a.status = :status
              AND (a.startDate IS NULL OR a.startDate <= :today)
              AND (a.endDate IS NULL OR a.endDate >= :today)
            ORDER BY a.createdAt DESC
            """)
    List<AdRequest> findActiveAds(@Param("status") AdStatus status, @Param("today") LocalDate today);
}
