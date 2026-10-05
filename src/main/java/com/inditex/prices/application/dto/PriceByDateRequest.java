package com.inditex.prices.application.dto;

import java.time.LocalDateTime;

public class PriceByDateRequest {
    private final Integer brandId;
    private final Integer productId;
    private final LocalDateTime applicationDate;

    public PriceByDateRequest(Integer brandId, Integer productId, LocalDateTime applicationDate) {
        this.brandId = brandId;
        this.productId = productId;
        this.applicationDate = applicationDate;
    }

    public Integer getBrandId() { return brandId; }
    public Integer getProductId() { return productId; }
    public LocalDateTime getApplicationDate() { return applicationDate; }
}
