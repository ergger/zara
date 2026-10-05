package com.inditex.prices.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public record Money(BigDecimal amount, String currency) {

    public Money {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be non-negative");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency must be a valid ISO 4217 3-letter code");
        }
        String normalizedCurrency = currency.trim().toUpperCase(Locale.ROOT);
        if (normalizedCurrency.length() != 3) {
            throw new IllegalArgumentException("Currency must be a valid ISO 4217 3-letter code");
        }
        amount = amount.setScale(2, RoundingMode.HALF_UP);
        currency = normalizedCurrency;
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
}