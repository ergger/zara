package com.inditex.prices.application.usecase;

import com.inditex.prices.application.dto.PriceUpsertRequest;
import com.inditex.prices.application.dto.PriceUpsertResponse;
import com.inditex.prices.application.mapper.PriceApplicationMapper;
import com.inditex.prices.domain.model.Money;
import com.inditex.prices.domain.model.Price;
import com.inditex.prices.domain.model.PriceChangeType;
import com.inditex.prices.domain.model.PriceChangedEvent;
import com.inditex.prices.domain.port.out.PriceChangedEventPublisher;
import com.inditex.prices.domain.port.out.PriceRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpsertPriceUseCaseTest {
    private static final LocalDateTime START = LocalDateTime.of(2020, 6, 14, 0, 0);
    private static final LocalDateTime END = LocalDateTime.of(2020, 12, 31, 23, 59, 59);

    @Mock
    private PriceRepositoryPort priceRepositoryPort;
    @Mock
    private PriceApplicationMapper mapper;
    @Mock
    private Money mockMoney;
    @Mock
    private PriceChangedEventPublisher priceChangedEventPublisher;
    @InjectMocks
    private UpsertPriceUseCase useCase;

    private static PriceUpsertRequest request(Integer priceList, Integer priority, BigDecimal price, String curr) {
        return new PriceUpsertRequest(1, 35455, priceList, START, END, priority, price, curr);
    }

    private static Price existingPrice() {
        return new Price(1L, 1, 35455, 1, START, END, 0, new Money(new BigDecimal("35.50"), "EUR"));
    }

    private static Price savedPrice(Integer priceList, Integer priority, String amount, String curr) {
        return new Price(1L, 1, 35455, priceList, START, END, priority, new Money(new BigDecimal(amount), curr));
    }

    private void stubResponseMapper() {
        when(mapper.toPriceUpsertResponse(any(), anyBoolean(), anyBoolean()))
                .thenAnswer(invocation -> new PriceUpsertResponse(
                        7L, 1, 35455, 1, START, END, 0, new BigDecimal("35.50"), "EUR",
                        invocation.getArgument(1, Boolean.class),
                        invocation.getArgument(2, Boolean.class)));
    }

    private PriceChangedEvent capturePublishedEvent() {
        ArgumentCaptor<PriceChangedEvent> captor = ArgumentCaptor.forClass(PriceChangedEvent.class);
        verify(priceChangedEventPublisher).publish(captor.capture());
        return captor.getValue();
    }

    @Test
    void shouldCreateWhenNotExists() {
        PriceUpsertRequest request = request(1, 0, new BigDecimal("35.50"), "EUR");
        Price price = new Price(1, 35455, 1, START, END, 0, new Money(new BigDecimal("35.50"), "EUR"));

        when(priceRepositoryPort.findByNaturalKey(1, 35455, START, END)).thenReturn(Optional.empty());
        when(mapper.toPrice(request)).thenReturn(price);
        when(priceRepositoryPort.save(price)).thenReturn(savedPrice(1, 0, "35.50", "EUR"));
        stubResponseMapper();

        PriceUpsertResponse response = useCase.upsertPrice(request);
        assertTrue(response.isCreated());
        assertFalse(response.isUpdated());
    }

    @Test
    void shouldPublishCreatedEventWhenCreating() {
        PriceUpsertRequest request = request(1, 0, new BigDecimal("35.50"), "EUR");
        Price price = new Price(1, 35455, 1, START, END, 0, new Money(new BigDecimal("35.50"), "EUR"));

        when(priceRepositoryPort.findByNaturalKey(1, 35455, START, END)).thenReturn(Optional.empty());
        when(mapper.toPrice(request)).thenReturn(price);
        when(priceRepositoryPort.save(price)).thenReturn(savedPrice(1, 0, "35.50", "EUR"));
        stubResponseMapper();

        useCase.upsertPrice(request);

        PriceChangedEvent event = capturePublishedEvent();
        assertEquals(PriceChangeType.CREATED, event.changeType());
        assertEquals(1L, event.priceId());
        assertEquals(1, event.brandId());
        assertEquals(35455, event.productId());
        assertEquals(1, event.priceList());
        assertEquals(0, event.priority());
        assertEquals(START, event.startDate());
        assertEquals(END, event.endDate());
        assertEquals(new BigDecimal("35.50"), event.price().amount());
        assertEquals("EUR", event.price().currency());
        assertNotNull(event.occurredAt());
    }

    @Test
    void shouldReturnExistingWhenSame() {
        when(priceRepositoryPort.findByNaturalKey(1, 35455, START, END)).thenReturn(Optional.of(existingPrice()));
        stubResponseMapper();

        PriceUpsertResponse response = useCase.upsertPrice(request(1, 0, new BigDecimal("35.50"), "EUR"));

        assertFalse(response.isCreated());
        assertFalse(response.isUpdated());
        verify(priceRepositoryPort, never()).save(any());
        verify(priceChangedEventPublisher, never()).publish(any());
    }

    @Test
    void shouldUpdateWhenDifferent() {
        when(priceRepositoryPort.findByNaturalKey(1, 35455, START, END)).thenReturn(Optional.of(existingPrice()));
        when(priceRepositoryPort.save(any())).thenReturn(savedPrice(1, 0, "36.00", "EUR"));
        stubResponseMapper();

        PriceUpsertResponse response = useCase.upsertPrice(request(1, 0, new BigDecimal("36.00"), "EUR"));

        assertFalse(response.isCreated());
        assertTrue(response.isUpdated());
    }

    @Test
    void shouldPublishUpdatedEventWhenUpdating() {
        when(priceRepositoryPort.findByNaturalKey(1, 35455, START, END)).thenReturn(Optional.of(existingPrice()));
        when(priceRepositoryPort.save(any())).thenReturn(savedPrice(1, 0, "36.00", "EUR"));
        stubResponseMapper();

        useCase.upsertPrice(request(1, 0, new BigDecimal("36.00"), "EUR"));

        PriceChangedEvent event = capturePublishedEvent();
        assertEquals(PriceChangeType.UPDATED, event.changeType());
        assertEquals(1L, event.priceId());
        assertEquals(1, event.priceList());
        assertEquals(0, event.priority());
        assertEquals(new BigDecimal("36.00"), event.price().amount());
        assertEquals("EUR", event.price().currency());
    }

    @Test
    void shouldUpdateWhenPriceListDiffers() {
        when(priceRepositoryPort.findByNaturalKey(1, 35455, START, END)).thenReturn(Optional.of(existingPrice()));
        when(priceRepositoryPort.save(any())).thenReturn(savedPrice(2, 0, "35.50", "EUR"));
        stubResponseMapper();

        PriceUpsertResponse response = useCase.upsertPrice(request(2, 0, new BigDecimal("35.50"), "EUR"));

        assertTrue(response.isUpdated());
    }

    @Test
    void shouldUpdateWhenPriorityDiffers() {
        when(priceRepositoryPort.findByNaturalKey(1, 35455, START, END)).thenReturn(Optional.of(existingPrice()));
        when(priceRepositoryPort.save(any())).thenReturn(savedPrice(1, 5, "35.50", "EUR"));
        stubResponseMapper();

        PriceUpsertResponse response = useCase.upsertPrice(request(1, 5, new BigDecimal("35.50"), "EUR"));

        assertTrue(response.isUpdated());
    }

    @Test
    void shouldUpdateWhenCurrencyDiffers() {
        when(priceRepositoryPort.findByNaturalKey(1, 35455, START, END)).thenReturn(Optional.of(existingPrice()));
        when(priceRepositoryPort.save(any())).thenReturn(savedPrice(1, 0, "35.50", "USD"));
        stubResponseMapper();

        PriceUpsertResponse response = useCase.upsertPrice(request(1, 0, new BigDecimal("35.50"), "USD"));

        assertTrue(response.isUpdated());
    }

    @Test
    void shouldReturnExistingWhenBothAmountsAreNull() {
        Price existingWithNullAmount = new Price(1L, 1, 35455, 1, START, END, 0, mockMoney);
        when(priceRepositoryPort.findByNaturalKey(1, 35455, START, END)).thenReturn(Optional.of(existingWithNullAmount));
        stubResponseMapper();

        PriceUpsertResponse response = useCase.upsertPrice(request(1, 0, null, null));

        assertFalse(response.isCreated());
        assertFalse(response.isUpdated());
        verify(priceRepositoryPort, never()).save(any());
        verify(priceChangedEventPublisher, never()).publish(any());
    }

    @Test
    void shouldThrowWhenRequestPriceIsNull() {
        when(priceRepositoryPort.findByNaturalKey(1, 35455, START, END)).thenReturn(Optional.of(existingPrice()));

        assertThrows(IllegalArgumentException.class, () -> useCase.upsertPrice(request(1, 0, null, "EUR")));
        verify(priceRepositoryPort, never()).save(any());
        verify(priceChangedEventPublisher, never()).publish(any());
    }

    @Test
    void shouldUpdateWhenExistingAmountIsNull() {
        Price existingWithNullAmount = new Price(1L, 1, 35455, 1, START, END, 0, mockMoney);
        when(priceRepositoryPort.findByNaturalKey(1, 35455, START, END)).thenReturn(Optional.of(existingWithNullAmount));
        when(priceRepositoryPort.save(any())).thenReturn(savedPrice(1, 0, "35.50", "EUR"));
        stubResponseMapper();

        PriceUpsertResponse response = useCase.upsertPrice(request(1, 0, new BigDecimal("35.50"), "EUR"));

        assertTrue(response.isUpdated());
    }
}