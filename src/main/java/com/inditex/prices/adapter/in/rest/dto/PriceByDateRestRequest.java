package com.inditex.prices.adapter.in.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;

@Schema(description = "Solicitud para obtener precio aplicable por fecha")
public class PriceByDateRestRequest {
    @NotNull
    @Schema(description = "Código de cadena (1 = ZARA)", example = "1")
    private Integer brandId;

    @NotNull
    @Schema(description = "Código de producto", example = "35455")
    private Integer productId;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Schema(description = "Fecha/hora de aplicación en ISO-8601. Es obligatorio incluir la zona: no hay zona por defecto. "
            + "En una query string codifica el '+' del offset como %2B, o bien usa el designador 'Z' de UTC. "
            + "Ejemplos válidos: 2020-06-14T10:00:00Z o 2020-06-14T10:00:00%2B02:00",
            example = "2020-06-14T10:00:00Z")
    private ZonedDateTime applicationDate;

    public Integer getBrandId() { return brandId; }
    public void setBrandId(Integer brandId) { this.brandId = brandId; }
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public ZonedDateTime getApplicationDate() { return applicationDate; }
    public void setApplicationDate(ZonedDateTime applicationDate) { this.applicationDate = applicationDate; }
}
