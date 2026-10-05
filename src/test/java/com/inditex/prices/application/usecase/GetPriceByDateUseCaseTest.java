package com.inditex.prices.application.usecase;

import com.inditex.prices.application.dto.PriceByDateRequest;
import com.inditex.prices.application.dto.PriceByDateResponse;
import com.inditex.prices.application.mapper.PriceApplicationMapper;
import com.inditex.prices.domain.model.Money;
import com.inditex.prices.domain.model.Price;
import com.inditex.prices.domain.port.out.PriceRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPriceByDateUseCaseTest {
    @Mock
    private PriceRepositoryPort priceRepositoryPort;
    @Mock
    private PriceApplicationMapper mapper;
    @InjectMocks
    private GetPriceByDateUseCase useCase;

    @Test
    void shouldReturnApplicablePrice() {
        LocalDateTime start = LocalDateTime.of(2020, 6, 14, 0, 0);
        LocalDateTime end = LocalDateTime.of(2020, 12, 31, 23, 59, 59);
        Price price = new Price(1, 35455, 1, start, end, 0, new Money(new BigDecimal("35.50"), "EUR"));
        PriceByDateResponse response = new PriceByDateResponse(35455, 1, 1, start, end, new BigDecimal("35.50"), "EUR");

        when(priceRepositoryPort.findApplicablePrice(eq(35455), eq(1), any())).thenReturn(Optional.of(price));
        when(mapper.toPriceByDateResponse(price)).thenReturn(response);

        PriceByDateRequest request = new PriceByDateRequest(1, 35455, LocalDateTime.of(2020, 6, 14, 10, 0));
        PriceByDateResponse result = useCase.findApplicablePrice(request);

        assertNotNull(result);
        assertEquals(35455, result.getProductId());
        verify(priceRepositoryPort).findApplicablePrice(35455, 1, request.getApplicationDate());
    }

    @Test
    void shouldThrowWhenNoApplicablePrice() {
        when(priceRepositoryPort.findApplicablePrice(any(), any(), any())).thenReturn(Optional.empty());
        PriceByDateRequest request = new PriceByDateRequest(1, 35455, LocalDateTime.of(2020, 6, 14, 10, 0));
        assertThrows(NoSuchElementException.class, () -> useCase.findApplicablePrice(request));
    }
}
