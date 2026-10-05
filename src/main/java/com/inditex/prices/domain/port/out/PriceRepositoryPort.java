package com.inditex.prices.domain.port.out;

import com.inditex.prices.domain.model.Price;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PriceRepositoryPort {
    Optional<Price> findApplicablePrice(Integer productId, Integer brandId, LocalDateTime applicationDate);
    Optional<Price> findByNaturalKey(Integer brandId, Integer productId, LocalDateTime startDate, LocalDateTime endDate);
    Price save(Price price);
}
