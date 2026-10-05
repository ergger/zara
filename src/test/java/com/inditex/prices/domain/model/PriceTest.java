package com.inditex.prices.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PriceTest {
    private static final LocalDateTime START = LocalDateTime.of(2020, 6, 14, 0, 0);
    private static final LocalDateTime END = LocalDateTime.of(2020, 12, 31, 23, 59, 59);
    private static final Money MONEY = new Money(new BigDecimal("35.50"), "EUR");

    private static Price validPrice() {
        return new Price(1, 35455, 1, START, END, 0, MONEY);
    }

    @Test
    void shouldCreateValidPrice() {
        Price price = validPrice();
        assertEquals(1, price.brandId());
        assertEquals(35455, price.productId());
        assertEquals(1, price.priceList());
        assertEquals(0, price.priority());
        assertEquals(START, price.startDate());
        assertEquals(END, price.endDate());
        assertEquals(MONEY, price.money());
        assertTrue(price.isApplicableAt(LocalDateTime.of(2020, 6, 14, 10, 0)));
        assertTrue(price.isApplicableAt(LocalDateTime.of(2020, 12, 31, 23, 59, 59)));
    }

    @Test
    void shouldRejectInvalidBrandId() {
        assertThrows(IllegalArgumentException.class, () -> new Price(null, 35455, 1, START, END, 0, MONEY));
        assertThrows(IllegalArgumentException.class, () -> new Price(0, 35455, 1, START, END, 0, MONEY));
        assertThrows(IllegalArgumentException.class, () -> new Price(-1, 35455, 1, START, END, 0, MONEY));
    }

    @Test
    void shouldRejectInvalidProductId() {
        assertThrows(IllegalArgumentException.class, () -> new Price(1, null, 1, START, END, 0, MONEY));
        assertThrows(IllegalArgumentException.class, () -> new Price(1, 0, 1, START, END, 0, MONEY));
        assertThrows(IllegalArgumentException.class, () -> new Price(1, -1, 1, START, END, 0, MONEY));
    }

    @Test
    void shouldRejectInvalidPriceList() {
        assertThrows(IllegalArgumentException.class, () -> new Price(1, 35455, null, START, END, 0, MONEY));
        assertThrows(IllegalArgumentException.class, () -> new Price(1, 35455, 0, START, END, 0, MONEY));
        assertThrows(IllegalArgumentException.class, () -> new Price(1, 35455, -1, START, END, 0, MONEY));
    }

    @Test
    void shouldRejectInvalidPriority() {
        assertThrows(IllegalArgumentException.class, () -> new Price(1, 35455, 1, START, END, null, MONEY));
        assertThrows(IllegalArgumentException.class, () -> new Price(1, 35455, 1, START, END, -1, MONEY));
    }

    @Test
    void shouldAcceptZeroPriority() {
        assertEquals(0, validPrice().priority());
    }

    @Test
    void shouldDetectNaturalKey() {
        Price price = validPrice();
        assertTrue(price.hasSameNaturalKey(1, 35455, START, END));
        assertFalse(price.hasSameNaturalKey(1, 35455, START, LocalDateTime.of(2020, 12, 31, 0, 0)));
    }

    @Test
    void shouldNotDetectNaturalKeyOnDifferentBrandProductOrStart() {
        Price price = validPrice();
        assertFalse(price.hasSameNaturalKey(2, 35455, START, END));
        assertFalse(price.hasSameNaturalKey(1, 35456, START, END));
        assertFalse(price.hasSameNaturalKey(1, 35455, LocalDateTime.of(2020, 6, 15, 0, 0), END));
    }

    @Test
    void shouldExposeIdOnlyWhenProvided() {
        assertNull(validPrice().id());
        assertEquals(7L, new Price(7L, 1, 35455, 1, START, END, 0, MONEY).id());
    }

    @Test
    void shouldBeEqualWhenAllFieldsMatch() {
        Price price = validPrice();
        Price same = validPrice();
        assertEquals(price, price);
        assertEquals(price, same);
        assertEquals(price.hashCode(), same.hashCode());
    }

    @Test
    void shouldNotBeEqualOnNullOrDifferentType() {
        Price price = validPrice();
        assertNotEquals(price, null);
        assertNotEquals(price, new Object());
    }

    @Test
    void shouldNotBeEqualWhenAnyFieldDiffers() {
        Money other = new Money(new BigDecimal("99.99"), "EUR");
        assertNotEquals(validPrice(), new Price(2, 35455, 1, START, END, 0, MONEY));
        assertNotEquals(validPrice(), new Price(1, 35456, 1, START, END, 0, MONEY));
        assertNotEquals(validPrice(), new Price(1, 35455, 2, START, END, 0, MONEY));
        assertNotEquals(validPrice(), new Price(1, 35455, 1, LocalDateTime.of(2020, 6, 15, 0, 0), END, 0, MONEY));
        assertNotEquals(validPrice(), new Price(1, 35455, 1, START, LocalDateTime.of(2021, 1, 1, 0, 0), 0, MONEY));
        assertNotEquals(validPrice(), new Price(1, 35455, 1, START, END, 1, MONEY));
        assertNotEquals(validPrice(), new Price(1, 35455, 1, START, END, 0, other));
        assertNotEquals(validPrice(), new Price(7L, 1, 35455, 1, START, END, 0, MONEY));
    }
}