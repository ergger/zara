package com.inditex.prices.infrastructure.persistence.adapter;

import com.inditex.prices.domain.model.Money;
import com.inditex.prices.domain.model.Price;
import com.inditex.prices.domain.port.out.PriceRepositoryPort;
import com.inditex.prices.infrastructure.persistence.entity.PriceJpaEntity;
import com.inditex.prices.infrastructure.persistence.repository.PriceJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class PriceRepositoryAdapter implements PriceRepositoryPort {

    private final PriceJpaRepository priceJpaRepository;

    public PriceRepositoryAdapter(PriceJpaRepository priceJpaRepository) {
        this.priceJpaRepository = priceJpaRepository;
    }

    @Override
    public Optional<Price> findApplicablePrice(Integer productId, Integer brandId, LocalDateTime applicationDate) {
        return priceJpaRepository.findApplicablePrice(productId, brandId, applicationDate)
                .map(this::toDomain);
    }

    @Override
    public Optional<Price> findByNaturalKey(Integer brandId, Integer productId, LocalDateTime startDate, LocalDateTime endDate) {
        return priceJpaRepository.findByBrandIdAndProductIdAndStartDateAndEndDate(brandId, productId, startDate, endDate)
                .map(this::toDomain);
    }

    @Override
    public Price save(Price price) {
        PriceJpaEntity entity = toEntity(price);
        PriceJpaEntity saved = priceJpaRepository.save(entity);
        return toDomain(saved);
    }

    private Price toDomain(PriceJpaEntity entity) {
        return new Price(
                entity.getId(),
                entity.getBrandId(),
                entity.getProductId(),
                entity.getPriceList(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getPriority(),
                new Money(entity.getPrice(), entity.getCurr())
        );
    }

    private PriceJpaEntity toEntity(Price price) {
        PriceJpaEntity entity = new PriceJpaEntity();
        entity.setId(price.id());
        entity.setBrandId(price.brandId());
        entity.setProductId(price.productId());
        entity.setPriceList(price.priceList());
        entity.setStartDate(price.startDate());
        entity.setEndDate(price.endDate());
        entity.setPriority(price.priority());
        entity.setPrice(price.money().amount());
        entity.setCurr(price.money().currency());
        return entity;
    }
}
