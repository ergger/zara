package com.inditex.prices.adapter.in.rest;

import com.inditex.prices.adapter.in.rest.dto.PriceByDateRestRequest;
import com.inditex.prices.adapter.in.rest.dto.PriceByDateRestResponse;
import com.inditex.prices.adapter.in.rest.mapper.PriceRestMapper;
import com.inditex.prices.application.dto.PriceByDateRequest;
import com.inditex.prices.application.dto.PriceByDateResponse;
import com.inditex.prices.domain.port.in.PriceByDateQueryPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api/v1/prices")
@Tag(name = "Prices", description = "Operaciones relacionadas con precios")
public class PriceByDateController {

    private final PriceByDateQueryPort priceByDateQueryPort;
    private final PriceRestMapper mapper;

    public PriceByDateController(PriceByDateQueryPort priceByDateQueryPort, PriceRestMapper mapper) {
        this.priceByDateQueryPort = priceByDateQueryPort;
        this.mapper = mapper;
    }

    @GetMapping("/by-date")
    @Operation(summary = "Obtener precio aplicable por fecha",
            description = "Obtiene el precio aplicable para un producto/tienda en una fecha dada. "
                    + "Fechas inclusivas [START_DATE, END_DATE]. Desempate: PRIORITY DESC → START_DATE DESC ('más reciente') → clave única. "
                    + "La zona horaria es obligatoria (no hay zona por defecto). En una query string el '+' del offset debe ir "
                    + "codificado como %2B, o se puede usar el designador 'Z' de UTC: 2020-06-14T10:00:00Z.")
    @ApiResponse(responseCode = "200", description = "Precio encontrado", content = @Content(schema = @Schema(implementation = PriceByDateRestResponse.class)))
    @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content)
    @ApiResponse(responseCode = "404", description = "No existe precio aplicable", content = @Content)
    public ResponseEntity<PriceByDateRestResponse> getPriceByDate(
            @Parameter(description = "Código de cadena (1 = ZARA)", example = "1")
            @RequestParam Integer brandId,
            @Parameter(description = "Código de producto", example = "35455")
            @RequestParam Integer productId,
            @Parameter(description = "Fecha/hora de aplicación con zona horaria. Usar sufijo Z o codificar el offset como %2B",
                    example = "2020-06-14T10:00:00Z")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime applicationDate) {

        PriceByDateRestRequest request = new PriceByDateRestRequest();
        request.setBrandId(brandId);
        request.setProductId(productId);
        request.setApplicationDate(applicationDate);

        PriceByDateRequest appRequest = mapper.toPriceByDateRequest(request);
        PriceByDateResponse response = priceByDateQueryPort.findApplicablePrice(appRequest);
        return ResponseEntity.ok(mapper.toPriceByDateRestResponse(response));
    }
}
