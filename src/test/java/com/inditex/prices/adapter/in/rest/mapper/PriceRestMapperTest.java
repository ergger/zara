package com.inditex.prices.adapter.in.rest.mapper;

import com.inditex.prices.adapter.in.rest.dto.PriceByDateRestRequest;
import com.inditex.prices.adapter.in.rest.dto.PriceByDateRestResponse;
import com.inditex.prices.adapter.in.rest.dto.PriceUpsertRestRequest;
import com.inditex.prices.adapter.in.rest.dto.PriceUpsertRestResponse;
import com.inditex.prices.application.dto.PriceByDateRequest;
import com.inditex.prices.application.dto.PriceByDateResponse;
import com.inditex.prices.application.dto.PriceUpsertRequest;
import com.inditex.prices.application.dto.PriceUpsertResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PriceRestMapperTest {

    private final PriceRestMapper mapper = new PriceRestMapperImpl();

    private static PriceByDateRestRequest byDateRequest(ZonedDateTime applicationDate) {
        PriceByDateRestRequest request = new PriceByDateRestRequest();
        request.setBrandId(1);
        request.setProductId(35455);
        request.setApplicationDate(applicationDate);
        return request;
    }

    private static PriceUpsertRestRequest upsertRequest(ZonedDateTime startDate, ZonedDateTime endDate) {
        PriceUpsertRestRequest request = new PriceUpsertRestRequest();
        request.setBrandId(1);
        request.setProductId(35455);
        request.setPriceList(1);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setPriority(0);
        request.setPrice(new BigDecimal("35.50"));
        request.setCurr("EUR");
        return request;
    }

    @Test
    void shouldReturnNullLocalDateTimeForNullInput() {
        assertNull(mapper.toLocalDateTime(null));
    }

    @Test
    void shouldKeepWallClockFromTheOffsetSuppliedByClient() {
        ZonedDateTime zoned = ZonedDateTime.parse("2020-06-14T10:00:00+02:00");
        assertEquals(LocalDateTime.of(2020, 6, 14, 10, 0), mapper.toLocalDateTime(zoned));
    }

    @Test
    void shouldNotNormalizeToAnyConfiguredZone() {
        // Mismo instante, dos offsets distintos: el reloj de pared es el que mando el cliente.
        // No hay zona por defecto que pueda convertir uno en el otro.
        ZonedDateTime withOffset = ZonedDateTime.parse("2020-06-14T10:00:00+02:00");
        ZonedDateTime withUtc = ZonedDateTime.parse("2020-06-14T08:00:00Z");

        assertEquals(withOffset.toInstant(), withUtc.toInstant());
        assertEquals(LocalDateTime.of(2020, 6, 14, 10, 0), mapper.toLocalDateTime(withOffset));
        assertEquals(LocalDateTime.of(2020, 6, 14, 8, 0), mapper.toLocalDateTime(withUtc));
    }

    @Test
    void shouldMapByDateRequest() {
        PriceByDateRequest result = mapper.toPriceByDateRequest(
                byDateRequest(ZonedDateTime.parse("2020-06-14T10:00:00+02:00")));

        assertEquals(1, result.getBrandId());
        assertEquals(35455, result.getProductId());
        assertEquals(LocalDateTime.of(2020, 6, 14, 10, 0), result.getApplicationDate());
    }

    @Test
    void shouldMapByDateRequestWithoutApplicationDate() {
        assertNull(mapper.toPriceByDateRequest(byDateRequest(null)).getApplicationDate());
    }

    @Test
    void shouldReturnNullByDateRequestForNullSource() {
        assertNull(mapper.toPriceByDateRequest(null));
    }

    @Test
    void shouldMapByDateResponse() {
        PriceByDateResponse source = new PriceByDateResponse(
                35455, 1, 2, LocalDateTime.of(2020, 6, 14, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59, 59), new BigDecimal("35.50"), "EUR");

        PriceByDateRestResponse result = mapper.toPriceByDateRestResponse(source);

        assertEquals(35455, result.getProductId());
        assertEquals(1, result.getBrandId());
        assertEquals(2, result.getPriceList());
        assertEquals(LocalDateTime.of(2020, 6, 14, 0, 0), result.getStartDate());
        assertEquals(LocalDateTime.of(2020, 12, 31, 23, 59, 59), result.getEndDate());
        assertEquals(new BigDecimal("35.50"), result.getPrice());
        assertEquals("EUR", result.getCurr());
    }

    @Test
    void shouldReturnNullByDateResponseForNullSource() {
        assertNull(mapper.toPriceByDateRestResponse(null));
    }

    @Test
    void shouldMapUpsertRequest() {
        PriceUpsertRequest result = mapper.toPriceUpsertRequest(upsertRequest(
                ZonedDateTime.parse("2020-06-14T00:00:00+02:00"),
                ZonedDateTime.parse("2020-12-31T23:59:59+02:00")));

        assertEquals(1, result.getBrandId());
        assertEquals(35455, result.getProductId());
        assertEquals(1, result.getPriceList());
        assertEquals(LocalDateTime.of(2020, 6, 14, 0, 0), result.getStartDate());
        assertEquals(LocalDateTime.of(2020, 12, 31, 23, 59, 59), result.getEndDate());
        assertEquals(0, result.getPriority());
        assertEquals(new BigDecimal("35.50"), result.getPrice());
        assertEquals("EUR", result.getCurr());
    }

    @Test
    void shouldMapUpsertRequestWithoutDates() {
        PriceUpsertRequest result = mapper.toPriceUpsertRequest(upsertRequest(null, null));

        assertNull(result.getStartDate());
        assertNull(result.getEndDate());
    }

    @Test
    void shouldReturnNullUpsertRequestForNullSource() {
        assertNull(mapper.toPriceUpsertRequest(null));
    }

    @Test
    void shouldMapUpsertResponse() {
        PriceUpsertResponse source = new PriceUpsertResponse(
                7L, 1, 35455, 1, LocalDateTime.of(2020, 6, 14, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59, 59), 0,
                new BigDecimal("35.50"), "EUR", true, false);

        PriceUpsertRestResponse result = mapper.toPriceUpsertRestResponse(source);

        assertEquals(7L, result.getId());
        assertEquals(1, result.getBrandId());
        assertEquals(35455, result.getProductId());
        assertEquals(1, result.getPriceList());
        assertEquals(LocalDateTime.of(2020, 6, 14, 0, 0), result.getStartDate());
        assertEquals(LocalDateTime.of(2020, 12, 31, 23, 59, 59), result.getEndDate());
        assertEquals(0, result.getPriority());
        assertEquals(new BigDecimal("35.50"), result.getPrice());
        assertEquals("EUR", result.getCurr());
        assertTrue(result.isCreated());
        assertFalse(result.isUpdated());
    }

    @Test
    void shouldReturnNullUpsertResponseForNullSource() {
        assertNull(mapper.toPriceUpsertRestResponse(null));
    }
}