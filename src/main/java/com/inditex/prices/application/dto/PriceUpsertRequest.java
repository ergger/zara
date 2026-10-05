package com.inditex.prices.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PriceUpsertRequest {
    private final Integer brandId;
    private final Integer productId;
    private final Integer priceList;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Integer priority;
    private final BigDecimal price;
    private final String curr;

    public PriceUpsertRequest(Integer brandId, Integer productId, Integer priceList,
                             LocalDateTime startDate, LocalDateTime endDate,
                             Integer priority, BigDecimal price, String curr) {
        this.brandId = brandId;
        this.productId = productId;
        this.priceList = priceList;
        this.startDate = startDate;
        this.endDate = endDate;
        this.priority = priority;
        this.price = price;
        this.curr = curr;
    }

    public Integer getBrandId() { return brandId; }
    public Integer getProductId() { return productId; }
    public Integer getPriceList() { return priceList; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public Integer getPriority() { return priority; }
    public BigDecimal getPrice() { return price; }
    public String getCurr() { return curr; }
}
