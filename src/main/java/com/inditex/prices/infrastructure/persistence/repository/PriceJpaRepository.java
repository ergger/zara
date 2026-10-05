package com.inditex.prices.infrastructure.persistence.repository;

import com.inditex.prices.infrastructure.persistence.entity.PriceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PriceJpaRepository extends JpaRepository<PriceJpaEntity, Long> {
    @Query(value = "SELECT * FROM ZARA.PRICES p " +
            "WHERE p.PRODUCT_ID = :productId " +
            "AND p.BRAND_ID = :brandId " +
            "AND :applicationDate >= p.START_DATE " +
            "AND :applicationDate <= p.END_DATE " +
            "ORDER BY p.PRIORITY DESC, p.START_DATE DESC " +
            "LIMIT 1", nativeQuery = true)
    Optional<PriceJpaEntity> findApplicablePrice(
            @Param("productId") Integer productId,
            @Param("brandId") Integer brandId,
            @Param("applicationDate") LocalDateTime applicationDate);

    Optional<PriceJpaEntity> findByBrandIdAndProductIdAndStartDateAndEndDate(
            Integer brandId, Integer productId, LocalDateTime startDate, LocalDateTime endDate);
}
