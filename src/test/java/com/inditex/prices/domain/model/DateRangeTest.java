package com.inditex.prices.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DateRangeTest {
    private static final LocalDateTime START = LocalDateTime.of(2020, 6, 14, 0, 0);
    private static final LocalDateTime END = LocalDateTime.of(2020, 12, 31, 23, 59, 59);

    @Test
    void shouldContainInclusiveDates() {
        DateRange range = new DateRange(START, END);
        assertTrue(range.contains(START));
        assertTrue(range.contains(END));
        assertTrue(range.contains(LocalDateTime.of(2020, 6, 15, 0, 0)));
        assertFalse(range.contains(LocalDateTime.of(2020, 12, 31, 23, 59, 59, 1)));
        assertFalse(range.contains(LocalDateTime.of(2020, 6, 13, 23, 59, 59)));
    }

    @Test
    void shouldExposeBounds() {
        DateRange range = new DateRange(START, END);
        assertEquals(START, range.startDate());
        assertEquals(END, range.endDate());
    }

    @Test
    void shouldAcceptEqualBounds() {
        DateRange range = new DateRange(START, START);
        assertTrue(range.contains(START));
    }

    @Test
    void shouldRejectInvalidRange() {
        LocalDateTime start = LocalDateTime.of(2020, 12, 31, 0, 0);
        LocalDateTime end = LocalDateTime.of(2020, 6, 14, 0, 0);
        assertThrows(IllegalArgumentException.class, () -> new DateRange(start, end));
        assertThrows(IllegalArgumentException.class, () -> new DateRange(null, end));
        assertThrows(IllegalArgumentException.class, () -> new DateRange(start, null));
        assertThrows(IllegalArgumentException.class, () -> new DateRange(null, null));
    }

    @Test
    void shouldNotContainNullDate() {
        assertFalse(new DateRange(START, END).contains(null));
    }

    @Test
    void shouldBeEqualWhenBoundsMatch() {
        DateRange range = new DateRange(START, END);
        DateRange same = new DateRange(START, END);
        assertEquals(range, range);
        assertEquals(range, same);
        assertEquals(range.hashCode(), same.hashCode());
    }

    @Test
    void shouldNotBeEqualOnNullOrDifferentType() {
        DateRange range = new DateRange(START, END);
        assertNotEquals(range, null);
        assertNotEquals(range, new Object());
    }

    @Test
    void shouldNotBeEqualWhenBoundsDiffer() {
        DateRange range = new DateRange(START, END);
        assertNotEquals(range, new DateRange(LocalDateTime.of(2020, 6, 15, 0, 0), END));
        assertNotEquals(range, new DateRange(START, LocalDateTime.of(2021, 1, 1, 0, 0)));
    }

    @Test
    void shouldRenderToString() {
        assertEquals(START + " - " + END, new DateRange(START, END).toString());
    }
}