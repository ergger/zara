package com.inditex.prices.application.mapper;

import com.inditex.prices.application.dto.PriceByDateResponse;
import com.inditex.prices.application.dto.PriceUpsertRequest;
import com.inditex.prices.application.dto.PriceUpsertResponse;
import com.inditex.prices.domain.model.Money;
import com.inditex.prices.domain.model.Price;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PriceApplicationMapperTest {
    private static final LocalDateTime START = LocalDateTime.of(2020, 6, 14, 0, 0);
    private static final LocalDateTime END = LocalDateTime.of(2020, 12, 31, 23, 59, 59);
    private static final Money MONEY = new Money(new BigDecimal("35.50"), "EUR");

    private static PriceUpsertRequest upsertRequest() {
        return new PriceUpsertRequest(1, 35455, 1, START, END, 0, new BigDecimal("35.50"), "EUR");
    }

    @Test
    void shouldMapPriceToByDateResponse() {
        PriceByDateResponse response = new PriceApplicationMapper()
                .toPriceByDateResponse(new Price(1, 35455, 2, START, END, 3, MONEY));

        assertEquals(35455, response.getProductId());
        assertEquals(1, response.getBrandId());
        assertEquals(2, response.getPriceList());
        assertEquals(START, response.getStartDate());
        assertEquals(END, response.getEndDate());
        assertEquals(new BigDecimal("35.50"), response.getPrice());
        assertEquals("EUR", response.getCurr());
    }

    @Test
    void shouldMapPriceWithoutMoneyToByDateResponse() {
        PriceByDateResponse response = new PriceApplicationMapper()
                .toPriceByDateResponse(new Price(1, 35455, 2, START, END, 3, null));

        assertNull(response.getPrice());
        assertNull(response.getCurr());
    }

    @Test
    void shouldReturnNullByDateResponseForNullPrice() {
        assertNull(new PriceApplicationMapper().toPriceByDateResponse(null));
    }

    @Test
    void shouldMapUpsertRequestToPrice() {
        Price price = new PriceApplicationMapper().toPrice(upsertRequest());

        assertNull(price.id());
        assertEquals(1, price.brandId());
        assertEquals(35455, price.productId());
        assertEquals(1, price.priceList());
        assertEquals(START, price.startDate());
        assertEquals(END, price.endDate());
        assertEquals(0, price.priority());
        assertEquals(new BigDecimal("35.50"), price.money().amount());
        assertEquals("EUR", price.money().currency());
    }

    @Test
    void shouldReturnNullPriceForNullRequest() {
        assertNull(new PriceApplicationMapper().toPrice(null));
    }

    @Test
    void shouldMapPriceToUpsertResponseAsUpdate() {
        PriceUpsertResponse response = new PriceApplicationMapper()
                .toPriceUpsertResponse(new Price(7L, 1, 35455, 1, START, END, 0, MONEY), false, true);

        assertEquals(7L, response.getId());
        assertEquals(1, response.getBrandId());
        assertEquals(35455, response.getProductId());
        assertEquals(1, response.getPriceList());
        assertEquals(START, response.getStartDate());
        assertEquals(END, response.getEndDate());
        assertEquals(0, response.getPriority());
        assertEquals(new BigDecimal("35.50"), response.getPrice());
        assertEquals("EUR", response.getCurr());
        assertFalse(response.isCreated());
        assertTrue(response.isUpdated());
    }

    @Test
    void shouldMapPriceToUpsertResponseAsCreate() {
        PriceUpsertResponse response = new PriceApplicationMapper()
                .toPriceUpsertResponse(new Price(7L, 1, 35455, 1, START, END, 0, MONEY), true, false);

        assertTrue(response.isCreated());
        assertFalse(response.isUpdated());
    }

    @Test
    void shouldMapPriceToUpsertResponseWithoutChanges() {
        PriceUpsertResponse response = new PriceApplicationMapper()
                .toPriceUpsertResponse(new Price(7L, 1, 35455, 1, START, END, 0, MONEY), false, false);

        assertFalse(response.isCreated());
        assertFalse(response.isUpdated());
    }

    @Test
    void shouldMapPriceWithoutMoneyToUpsertResponse() {
        PriceUpsertResponse response = new PriceApplicationMapper()
                .toPriceUpsertResponse(new Price(7L, 1, 35455, 1, START, END, 0, null), true, false);

        assertNull(response.getPrice());
        assertNull(response.getCurr());
    }

    @Test
    void shouldReturnNullUpsertResponseForNullPrice() {
        assertNull(new PriceApplicationMapper().toPriceUpsertResponse(null, true, false));
    }
}