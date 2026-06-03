package com.portafolio.analytics.infrastructure.repository;

import com.portafolio.analytics.domain.model.BidAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BidAnalyticsRepository extends JpaRepository<BidAnalytics, Long> {
}