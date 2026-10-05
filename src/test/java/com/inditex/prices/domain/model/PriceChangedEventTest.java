package com.inditex.prices.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PriceChangedEventTest {
    private static final LocalDateTime START = LocalDateTime.of(2020, 6, 14, 0, 0);
    private static final LocalDateTime END = LocalDateTime.of(2020, 12, 31, 23, 59, 59);

    private static Price price(Integer priceList, Integer priority, String amount) {
        return new Price(7L, 1, 35455, priceList, START, END, priority, new Money(new BigDecimal(amount), "EUR"));
    }

    @Test
    void shouldBuildCreatedEventFromSavedPrice() {
        PriceChangedEvent event = PriceChangedEvent.created(price(2, 3, "35.50"));

        assertEquals(PriceChangeType.CREATED, event.changeType());
        assertEquals(7L, event.priceId());
        assertEquals(1, event.brandId());
        assertEquals(35455, event.productId());
        assertEquals(2, event.priceList());
        assertEquals(3, event.priority());
        assertEquals(new BigDecimal("35.50"), event.price().amount());
        assertEquals(START, event.startDate());
        assertEquals(END, event.endDate());
        assertNotNull(event.occurredAt());
    }

    @Test
    void shouldBuildUpdatedEventFromSavedPrice() {
        PriceChangedEvent event = PriceChangedEvent.updated(price(2, 3, "41.75"));

        assertEquals(PriceChangeType.UPDATED, event.changeType());
        assertEquals(7L, event.priceId());
        assertEquals(2, event.priceList());
        assertEquals(3, event.priority());
        assertEquals(new BigDecimal("41.75"), event.price().amount());
        assertEquals("EUR", event.price().currency());
        assertEquals(START, event.startDate());
        assertEquals(END, event.endDate());
        assertNotNull(event.occurredAt());
    }

    @Test
    void shouldExposeEnumsForEveryChangeType() {
        assertEquals(2, PriceChangeType.values().length);
        assertEquals(PriceChangeType.CREATED, PriceChangeType.valueOf("CREATED"));
        assertEquals(PriceChangeType.UPDATED, PriceChangeType.valueOf("UPDATED"));
    }
}