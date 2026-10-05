package com.inditex.prices.adapter.in.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Schema(description = "Solicitud para upsert lógico idempotente de precio")
public class PriceUpsertRestRequest {
    @NotNull
    @Schema(example = "1")
    private Integer brandId;

    @NotNull
    @Schema(example = "35455")
    private Integer productId;

    @NotNull
    @Positive
    @Schema(example = "1")
    private Integer priceList;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Schema(example = "2020-06-14T00:00:00+02:00")
    private ZonedDateTime startDate;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Schema(example = "2020-12-31T23:59:59+02:00")
    private ZonedDateTime endDate;

    @NotNull
    @Schema(example = "1")
    private Integer priority;

    @NotNull
    @Schema(example = "35.50")
    private BigDecimal price;

    @NotNull
    @Size(min = 3, max = 3)
    @Schema(example = "EUR")
    private String curr;

    public Integer getBrandId() { return brandId; }
    public void setBrandId(Integer brandId) { this.brandId = brandId; }
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public Integer getPriceList() { return priceList; }
    public void setPriceList(Integer priceList) { this.priceList = priceList; }
    public ZonedDateTime getStartDate() { return startDate; }
    public void setStartDate(ZonedDateTime startDate) { this.startDate = startDate; }
    public ZonedDateTime getEndDate() { return endDate; }
    public void setEndDate(ZonedDateTime endDate) { this.endDate = endDate; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getCurr() { return curr; }
    public void setCurr(String curr) { this.curr = curr; }
}
