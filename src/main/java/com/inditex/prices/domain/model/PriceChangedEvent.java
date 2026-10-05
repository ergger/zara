package com.inditex.prices.domain.model;

import java.time.Instant;
import java.time.LocalDateTime;

public record PriceChangedEvent(
        PriceChangeType changeType,
        Long priceId,
        Integer brandId,
        Integer productId,
        Integer priceList,
        Integer priority,
        Money price,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Instant occurredAt
) {

    public static PriceChangedEvent created(Price saved) {
        return new PriceChangedEvent(
                PriceChangeType.CREATED,
                saved.id(),
                saved.brandId(),
                saved.productId(),
                saved.priceList(),
                saved.priority(),
                saved.money(),
                saved.startDate(),
                saved.endDate(),
                Instant.now()
        );
    }

    public static PriceChangedEvent updated(Price saved) {
        return new PriceChangedEvent(
                PriceChangeType.UPDATED,
                saved.id(),
                saved.brandId(),
                saved.productId(),
                saved.priceList(),
                saved.priority(),
                saved.money(),
                saved.startDate(),
                saved.endDate(),
                Instant.now()
        );
    }
}