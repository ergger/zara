package com.inditex.prices.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {
    @Test
    void shouldCreateValidMoney() {
        Money money = new Money(new BigDecimal("35.50"), "EUR");
        assertEquals(new BigDecimal("35.50"), money.amount());
        assertEquals("EUR", money.currency());
    }

    @Test
    void shouldRejectNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> new Money(new BigDecimal("-1"), "EUR"));
    }

    @Test
    void shouldRejectNullAmount() {
        assertThrows(IllegalArgumentException.class, () -> new Money(null, "EUR"));
    }

    @Test
    void shouldRejectInvalidCurrency() {
        assertThrows(IllegalArgumentException.class, () -> new Money(new BigDecimal("10"), "EU"));
        assertThrows(IllegalArgumentException.class, () -> new Money(new BigDecimal("10"), null));
    }

    @Test
    void shouldRejectBlankCurrency() {
        assertThrows(IllegalArgumentException.class, () -> new Money(new BigDecimal("10"), ""));
        assertThrows(IllegalArgumentException.class, () -> new Money(new BigDecimal("10"), "   "));
    }

    @Test
    void shouldNormalizeLowercaseCurrency() {
        assertEquals("EUR", new Money(new BigDecimal("10"), "eur").currency());
    }

    @Test
    void shouldNormalizeSurroundingWhitespace() {
        assertEquals("EUR", new Money(new BigDecimal("10"), " eur ").currency());
        assertEquals(new BigDecimal("10.00"), new Money(new BigDecimal("10"), " EUR ").amount());
    }

    @Test
    void shouldRejectWhitespaceOnlyCurrency() {
        assertThrows(IllegalArgumentException.class, () -> new Money(new BigDecimal("10"), "    "));
    }

    @Test
    void shouldScaleAmount() {
        Money money = new Money(new BigDecimal("35.555"), "EUR");
        assertEquals(new BigDecimal("35.56"), money.amount());
    }

    @Test
    void shouldBeEqualWhenAmountAndCurrencyMatch() {
        Money money = new Money(new BigDecimal("35.50"), "EUR");
        Money same = new Money(new BigDecimal("35.50"), "EUR");
        assertEquals(money, money);
        assertEquals(money, same);
        assertEquals(money.hashCode(), same.hashCode());
    }

    @Test
    void shouldNotBeEqualOnNullOrDifferentType() {
        Money money = new Money(new BigDecimal("35.50"), "EUR");
        assertNotEquals(money, null);
        assertNotEquals(money, new Object());
    }

    @Test
    void shouldNotBeEqualWhenAmountOrCurrencyDiffers() {
        Money money = new Money(new BigDecimal("35.50"), "EUR");
        assertNotEquals(money, new Money(new BigDecimal("99.99"), "EUR"));
        assertNotEquals(money, new Money(new BigDecimal("35.50"), "USD"));
    }

    @Test
    void shouldRenderToString() {
        assertEquals("35.50 EUR", new Money(new BigDecimal("35.50"), "EUR").toString());
    }
}