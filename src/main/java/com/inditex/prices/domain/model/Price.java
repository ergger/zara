package com.inditex.prices.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public record Price(Long id, Integer brandId, Integer productId, Integer priceList,
                    DateRange dateRange, Integer priority, Money money) {

    public Price {
        if (brandId == null || brandId <= 0) {
            throw new IllegalArgumentException("BrandId must be positive");
        }
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("ProductId must be positive");
        }
        if (priceList == null || priceList <= 0) {
            throw new IllegalArgumentException("PriceList must be positive");
        }
        if (priority == null || priority < 0) {
            throw new IllegalArgumentException("Priority must be non-negative");
        }
    }

    public Price(Long id, Integer brandId, Integer productId, Integer priceList,
                 LocalDateTime startDate, LocalDateTime endDate, Integer priority, Money money) {
        this(id, brandId, productId, priceList, new DateRange(startDate, endDate), priority, money);
    }

    public Price(Integer brandId, Integer productId, Integer priceList,
                 LocalDateTime startDate, LocalDateTime endDate, Integer priority, Money money) {
        this(null, brandId, productId, priceList, new DateRange(startDate, endDate), priority, money);
    }

    public boolean isApplicableAt(LocalDateTime date) {
        return dateRange.contains(date);
    }

    public LocalDateTime startDate() {
        return dateRange.startDate();
    }

    public LocalDateTime endDate() {
        return dateRange.endDate();
    }

    public boolean hasSameNaturalKey(Integer brandId, Integer productId, LocalDateTime startDate, LocalDateTime endDate) {
        return Objects.equals(this.brandId, brandId) &&
                Objects.equals(this.productId, productId) &&
                Objects.equals(this.startDate(), startDate) &&
                Objects.equals(this.endDate(), endDate);
    }
}