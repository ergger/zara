package com.inditex.prices.adapter.in.rest;

import com.inditex.prices.adapter.in.rest.dto.PriceUpsertRestRequest;
import com.inditex.prices.adapter.in.rest.dto.PriceUpsertRestResponse;
import com.inditex.prices.adapter.in.rest.mapper.PriceRestMapper;
import com.inditex.prices.application.dto.PriceUpsertRequest;
import com.inditex.prices.application.dto.PriceUpsertResponse;
import com.inditex.prices.domain.port.in.PriceUpsertPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/prices")
@Tag(name = "Prices", description = "Operaciones relacionadas con precios")
public class PriceUpsertController {

    private final PriceUpsertPort priceUpsertPort;
    private final PriceRestMapper mapper;

    public PriceUpsertController(PriceUpsertPort priceUpsertPort, PriceRestMapper mapper) {
        this.priceUpsertPort = priceUpsertPort;
        this.mapper = mapper;
    }

    @PutMapping
    @Operation(summary = "Upsert lógico idempotente",
            description = "Crea, actualiza o devuelve existente. Si clave natural existe con campos distintos → UPDATE. Si idéntico → devolver existente. Si no existe → CREATE.")
    @ApiResponse(responseCode = "200", description = "Actualizado o existente sin cambios")
    @ApiResponse(responseCode = "201", description = "Creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    public ResponseEntity<PriceUpsertRestResponse> upsertPrice(@Valid @RequestBody PriceUpsertRestRequest request) {
        PriceUpsertRequest appRequest = mapper.toPriceUpsertRequest(request);
        PriceUpsertResponse response = priceUpsertPort.upsertPrice(appRequest);
        PriceUpsertRestResponse restResponse = mapper.toPriceUpsertRestResponse(response);
        if (response.isCreated()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(restResponse);
        }
        return ResponseEntity.ok(restResponse);
    }
}
