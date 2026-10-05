package com.inditex.prices.adapter.in.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Respuesta con precio aplicable")
public class PriceByDateRestResponse {
    @Schema(example = "35455")
    private Integer productId;
    @Schema(example = "1")
    private Integer brandId;
    @Schema(example = "1")
    private Integer priceList;
    @Schema(example = "2020-06-14T00:00:00")
    private LocalDateTime startDate;
    @Schema(example = "2020-12-31T23:59:59")
    private LocalDateTime endDate;
    @Schema(example = "35.50")
    private BigDecimal price;
    @Schema(example = "EUR")
    private String curr;

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public Integer getBrandId() { return brandId; }
    public void setBrandId(Integer brandId) { this.brandId = brandId; }
    public Integer getPriceList() { return priceList; }
    public void setPriceList(Integer priceList) { this.priceList = priceList; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getCurr() { return curr; }
    public void setCurr(String curr) { this.curr = curr; }
}
