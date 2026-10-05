package com.inditex.prices.adapter.in.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración del endpoint contra la base de datos H2 en memoria,
 * inicializada por Flyway con los datos del enunciado (V2__insert_initial_prices.sql).
 *
 * No se mockea ningún puerto: se ejercitan de verdad la capa REST, el caso de uso,
 * el adaptador de persistencia y la consulta SQL con su desempate por prioridad.
 *
 * Las fechas viajan con el designador 'Z' porque PriceRestMapper descarta el offset
 * al convertir a LocalDateTime; de este modo la hora de reloj es exactamente la del enunciado.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PriceByDateEndpointIntegrationTest {

    private static final String URL = "/api/v1/prices/by-date";
    private static final int BRAND_ID = 1;
    private static final int PRODUCT_ID = 35455;

    @Autowired
    private MockMvc mockMvc;

    private ResultActions callEndpointAt(String applicationDate) throws Exception {
        return mockMvc.perform(get(URL)
                .param("brandId", String.valueOf(BRAND_ID))
                .param("productId", String.valueOf(PRODUCT_ID))
                .param("applicationDate", applicationDate));
    }

    private void assertScenario(String applicationDate, int priceList, String amount,
                                String startDate, String endDate) throws Exception {
        callEndpointAt(applicationDate)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(PRODUCT_ID))
                .andExpect(jsonPath("$.brandId").value(BRAND_ID))
                .andExpect(jsonPath("$.priceList").value(priceList))
                .andExpect(jsonPath("$.price").value(new BigDecimal(amount).doubleValue()))
                .andExpect(jsonPath("$.curr").value("EUR"))
                .andExpect(jsonPath("$.startDate").value(startDate))
                .andExpect(jsonPath("$.endDate").value(endDate));
    }

    @Test
    void test1ShouldReturnPriceList1At10OnJune14th() throws Exception {
        assertScenario("2020-06-14T10:00:00Z", 1, "35.50",
                "2020-06-14T00:00:00", "2020-12-31T23:59:59");
    }

    @Test
    void test2ShouldReturnPriceList2At16OnJune14thOverTheHigherPriorityTariff() throws Exception {
        assertScenario("2020-06-14T16:00:00Z", 2, "25.45",
                "2020-06-14T15:00:00", "2020-06-14T18:30:00");
    }

    @Test
    void test3ShouldReturnPriceList1At21OnJune14thWhenThePriorityTariffHasExpired() throws Exception {
        assertScenario("2020-06-14T21:00:00Z", 1, "35.50",
                "2020-06-14T00:00:00", "2020-12-31T23:59:59");
    }

    @Test
    void test4ShouldReturnPriceList3At10OnJune15th() throws Exception {
        assertScenario("2020-06-15T10:00:00Z", 3, "30.50",
                "2020-06-15T00:00:00", "2020-06-15T11:00:00");
    }

    @Test
    void test5ShouldReturnPriceList4At21OnJune16th() throws Exception {
        assertScenario("2020-06-16T21:00:00Z", 4, "38.95",
                "2020-06-15T16:00:00", "2020-12-31T23:59:59");
    }
}
