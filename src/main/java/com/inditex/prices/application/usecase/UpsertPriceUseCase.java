package com.inditex.prices.application.usecase;

import com.inditex.prices.application.dto.PriceUpsertRequest;
import com.inditex.prices.application.dto.PriceUpsertResponse;
import com.inditex.prices.application.mapper.PriceApplicationMapper;
import com.inditex.prices.domain.model.Money;
import com.inditex.prices.domain.model.Price;
import com.inditex.prices.domain.model.PriceChangedEvent;
import com.inditex.prices.domain.port.in.PriceUpsertPort;
import com.inditex.prices.domain.port.out.PriceChangedEventPublisher;
import com.inditex.prices.domain.port.out.PriceRepositoryPort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class UpsertPriceUseCase implements PriceUpsertPort {

    private final PriceRepositoryPort priceRepositoryPort;
    private final PriceApplicationMapper mapper;
    private final PriceChangedEventPublisher priceChangedEventPublisher;

    public UpsertPriceUseCase(PriceRepositoryPort priceRepositoryPort,
                              PriceApplicationMapper mapper,
                              PriceChangedEventPublisher priceChangedEventPublisher) {
        this.priceRepositoryPort = priceRepositoryPort;
        this.mapper = mapper;
        this.priceChangedEventPublisher = priceChangedEventPublisher;
    }

    @Override
    public PriceUpsertResponse upsertPrice(PriceUpsertRequest request) {
        Optional<Price> existing = priceRepositoryPort.findByNaturalKey(
                request.getBrandId(),
                request.getProductId(),
                request.getStartDate(),
                request.getEndDate()
        );

        if (existing.isEmpty()) {
            // Crear nuevo registro
            Price newPrice = mapper.toPrice(request);
            Price saved = priceRepositoryPort.save(newPrice);
            priceChangedEventPublisher.publish(PriceChangedEvent.created(saved));
            return mapper.toPriceUpsertResponse(saved, true, false);
        }

        Price existingPrice = existing.get();
        boolean isSame = isSamePrice(existingPrice, request);

        if (isSame) {
            // Devolver registro existente sin cambios (idempotente)
            return mapper.toPriceUpsertResponse(existingPrice, false, false);
        }

        // Actualizar registro existente
        Price updatedPrice = new Price(
                existingPrice.id(),
                request.getBrandId(),
                request.getProductId(),
                request.getPriceList(),
                request.getStartDate(),
                request.getEndDate(),
                request.getPriority(),
                new Money(request.getPrice(), request.getCurr())
        );
        Price saved = priceRepositoryPort.save(updatedPrice);
        priceChangedEventPublisher.publish(PriceChangedEvent.updated(saved));
        return mapper.toPriceUpsertResponse(saved, false, true);
    }

    private boolean isSamePrice(Price existing, PriceUpsertRequest request) {
        return Objects.equals(existing.priceList(), request.getPriceList()) &&
                Objects.equals(existing.priority(), request.getPriority()) &&
                pricesEqual(existing.money().amount(), request.getPrice()) &&
                Objects.equals(existing.money().currency(), request.getCurr());
    }

    private boolean pricesEqual(BigDecimal a, BigDecimal b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.compareTo(b) == 0;
    }
}
