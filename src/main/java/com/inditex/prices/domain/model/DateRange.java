package com.inditex.prices.domain.model;

import java.time.LocalDateTime;

public record DateRange(LocalDateTime startDate, LocalDateTime endDate) {

    public DateRange {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date must not be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
    }

    public boolean contains(LocalDateTime date) {
        if (date == null) {
            return false;
        }
        // Fechas inclusivas: date >= startDate AND date <= endDate
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    @Override
    public String toString() {
        return startDate + " - " + endDate;
    }
}