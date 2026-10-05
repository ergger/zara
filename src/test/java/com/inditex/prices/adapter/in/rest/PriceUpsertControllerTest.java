package com.inditex.prices.adapter.in.rest;

import com.inditex.prices.adapter.in.rest.mapper.PriceRestMapperImpl;
import com.inditex.prices.application.dto.PriceUpsertResponse;
import com.inditex.prices.domain.port.in.PriceUpsertPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PriceUpsertController.class)
@Import(PriceRestMapperImpl.class)
class PriceUpsertControllerTest {
    private static final String URL = "/api/v1/prices";
    private static final String VALID_BODY = """
            {
              "brandId": 1,
              "productId": 35455,
              "priceList": 1,
              "startDate": "2020-06-14T00:00:00+02:00",
              "endDate": "2020-12-31T23:59:59+02:00",
              "priority": 0,
              "price": 35.50,
              "curr": "EUR"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PriceUpsertPort priceUpsertPort;


    private static PriceUpsertResponse response(boolean created, boolean updated) {
        return new PriceUpsertResponse(
                1L, 1, 35455, 1,
                LocalDateTime.of(2020, 6, 14, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59, 59),
                0, new BigDecimal("35.50"), "EUR", created, updated);
    }

    @Test
    void shouldReturn201WhenCreated() throws Exception {
        when(priceUpsertPort.upsertPrice(any())).thenReturn(response(true, false));

        mockMvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.price").value(35.50))
                .andExpect(jsonPath("$.curr").value("EUR"))
                .andExpect(jsonPath("$.created").value(true))
                .andExpect(jsonPath("$.updated").value(false));
    }

    @Test
    void shouldReturn200WhenUpdated() throws Exception {
        when(priceUpsertPort.upsertPrice(any())).thenReturn(response(false, true));

        mockMvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.created").value(false))
                .andExpect(jsonPath("$.updated").value(true));
    }

    @Test
    void shouldReturn200WhenExistingIsReturnedUnchanged() throws Exception {
        when(priceUpsertPort.upsertPrice(any())).thenReturn(response(false, false));

        mockMvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.created").value(false))
                .andExpect(jsonPath("$.updated").value(false));
    }

    @Test
    void shouldReturn400WhenBodyFailsValidation() throws Exception {
        String body = VALID_BODY.replace("\"price\": 35.50", "\"price\": null");

        mockMvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Request body attributes do not match the expected request"));
    }

    @Test
    void shouldReturn400WhenCurrencyIsNotThreeLetters() throws Exception {
        String body = VALID_BODY.replace("\"EUR\"", "\"EU\"");

        mockMvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request body attributes do not match the expected request"));
    }

    @Test
    void shouldReturn400WhenPriceListIsNotPositive() throws Exception {
        String body = VALID_BODY.replace("\"priceList\": 1", "\"priceList\": 0");

        mockMvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request body attributes do not match the expected request"));
    }

    @Test
    void shouldReturn400WhenDomainRejectsRequest() throws Exception {
        when(priceUpsertPort.upsertPrice(any()))
                .thenThrow(new IllegalArgumentException("Amount must be non-negative"));

        mockMvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Amount must be non-negative"));
    }
}