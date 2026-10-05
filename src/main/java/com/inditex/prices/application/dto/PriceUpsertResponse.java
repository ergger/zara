package com.inditex.prices.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PriceUpsertResponse {
    private final Long id;
    private final Integer brandId;
    private final Integer productId;
    private final Integer priceList;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Integer priority;
    private final BigDecimal price;
    private final String curr;
    private final boolean created;
    private final boolean updated;

    public PriceUpsertResponse(Long id, Integer brandId, Integer productId, Integer priceList,
                               LocalDateTime startDate, LocalDateTime endDate, Integer priority,
                               BigDecimal price, String curr, boolean created, boolean updated) {
        this.id = id;
        this.brandId = brandId;
        this.productId = productId;
        this.priceList = priceList;
        this.startDate = startDate;
        this.endDate = endDate;
        this.priority = priority;
        this.price = price;
        this.curr = curr;
        this.created = created;
        this.updated = updated;
    }

    public Long getId() { return id; }
    public Integer getBrandId() { return brandId; }
    public Integer getProductId() { return productId; }
    public Integer getPriceList() { return priceList; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public Integer getPriority() { return priority; }
    public BigDecimal getPrice() { return price; }
    public String getCurr() { return curr; }
    public boolean isCreated() { return created; }
    public boolean isUpdated() { return updated; }
}
