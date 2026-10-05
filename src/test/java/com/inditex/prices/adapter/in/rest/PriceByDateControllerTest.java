package com.inditex.prices.adapter.in.rest;

import com.inditex.prices.adapter.in.rest.mapper.PriceRestMapperImpl;
import com.inditex.prices.application.dto.PriceByDateResponse;
import com.inditex.prices.domain.port.in.PriceByDateQueryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PriceByDateController.class)
@Import(PriceRestMapperImpl.class)
class PriceByDateControllerTest {
    private static final String URL = "/api/v1/prices/by-date";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PriceByDateQueryPort priceByDateQueryPort;


    private static PriceByDateResponse response() {
        return new PriceByDateResponse(
                35455, 1, 1,
                LocalDateTime.of(2020, 6, 14, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59, 59),
                new BigDecimal("35.50"), "EUR");
    }

    @Test
    void shouldReturnApplicablePrice() throws Exception {
        when(priceByDateQueryPort.findApplicablePrice(any())).thenReturn(response());

        mockMvc.perform(get(URL)
                        .param("brandId", "1")
                        .param("productId", "35455")
                        .param("applicationDate", "2020-06-14T10:00:00+02:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.price").value(35.50))
                .andExpect(jsonPath("$.curr").value("EUR"));
    }

    @Test
    void shouldReturn404WhenNoApplicablePrice() throws Exception {
        when(priceByDateQueryPort.findApplicablePrice(any()))
                .thenThrow(new NoSuchElementException("No applicable price found"));

        mockMvc.perform(get(URL)
                        .param("brandId", "1")
                        .param("productId", "35455")
                        .param("applicationDate", "2020-06-14T10:00:00+02:00"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No applicable price found"));
    }

    @Test
    void shouldReturn400WhenDomainRejectsRequest() throws Exception {
        when(priceByDateQueryPort.findApplicablePrice(any()))
                .thenThrow(new IllegalArgumentException("BrandId must be positive"));

        mockMvc.perform(get(URL)
                        .param("brandId", "1")
                        .param("productId", "35455")
                        .param("applicationDate", "2020-06-14T10:00:00+02:00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("BrandId must be positive"));
    }

    @Test
    void shouldReturn400WithCleanMessageWhenDateCannotBeParsed() throws Exception {
        mockMvc.perform(get(URL)
                        .param("brandId", "1")
                        .param("productId", "35455")
                        .param("applicationDate", "no-es-una-fecha"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid value for parameter 'applicationDate': "
                        + "expected an ISO-8601 date-time such as 2020-06-14T10:00:00Z (UTC) "
                        + "or 2020-06-14T10:00:00%2B02:00 (offset encoded)"));
    }

    @Test
    void shouldReturn400WhenApplicationDateHasNoOffset() throws Exception {
        // Contrato de la Opcion A: el offset es obligatorio y no hay zona por defecto que lo supla.
        mockMvc.perform(get(URL)
                        .param("brandId", "1")
                        .param("productId", "35455")
                        .param("applicationDate", "2020-06-14T10:00:00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid value for parameter 'applicationDate': "
                        + "expected an ISO-8601 date-time such as 2020-06-14T10:00:00Z (UTC) "
                        + "or 2020-06-14T10:00:00%2B02:00 (offset encoded)"));
    }

    @Test
    void shouldExplainThatALiteralPlusIsDecodedAsSpace() throws Exception {
        // Un '+' sin codificar llega al servidor como espacio. El mensaje debe
        // explicar la causa y no repetir el formato que ha fallado.
        mockMvc.perform(get(URL)
                        .param("brandId", "1")
                        .param("productId", "35455")
                        .param("applicationDate", "2020-06-14T10:00:00 02:00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("decoded as a space")))
                .andExpect(jsonPath("$.message").value(containsString("%2B")));
    }

    @Test
    void shouldReturn400WithCleanMessageWhenNumericParameterIsNotANumber() throws Exception {
        mockMvc.perform(get(URL)
                        .param("brandId", "uno")
                        .param("productId", "35455")
                        .param("applicationDate", "2020-06-14T10:00:00+02:00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid value for parameter 'brandId': expected type Integer"));
    }

    @Test
    void shouldReturn400WithJsonBodyWhenRequiredParameterIsMissing() throws Exception {
        mockMvc.perform(get(URL)
                        .param("brandId", "1")
                        .param("productId", "35455"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Required request parameter 'applicationDate' "
                        + "for method parameter type ZonedDateTime is not present"));
    }

    @Test
    void shouldReturn400WithJsonBodyWhenBrandIdIsMissing() throws Exception {
        mockMvc.perform(get(URL)
                        .param("productId", "35455")
                        .param("applicationDate", "2020-06-14T10:00:00+02:00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("brandId")));
    }
}