package com.inditex.prices.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PriceByDateResponse {
    private final Integer productId;
    private final Integer brandId;
    private final Integer priceList;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final BigDecimal price;
    private final String curr;

    public PriceByDateResponse(Integer productId, Integer brandId, Integer priceList,
                              LocalDateTime startDate, LocalDateTime endDate,
                              BigDecimal price, String curr) {
        this.productId = productId;
        this.brandId = brandId;
        this.priceList = priceList;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.curr = curr;
    }

    public Integer getProductId() { return productId; }
    public Integer getBrandId() { return brandId; }
    public Integer getPriceList() { return priceList; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public BigDecimal getPrice() { return price; }
    public String getCurr() { return curr; }
}
