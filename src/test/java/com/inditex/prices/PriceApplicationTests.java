package com.inditex.prices;

import com.inditex.prices.adapter.in.rest.PriceByDateController;
import com.inditex.prices.application.usecase.GetPriceByDateUseCase;
import com.inditex.prices.application.usecase.UpsertPriceUseCase;
import com.inditex.prices.domain.port.out.PriceRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class PriceApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertNotNull(context);
    }

    @Test
    void wiresTheWholePriceFlow() {
        assertNotNull(context.getBean(PriceByDateController.class));
        assertNotNull(context.getBean(GetPriceByDateUseCase.class));
        assertNotNull(context.getBean(UpsertPriceUseCase.class));
        assertNotNull(context.getBean(PriceRepositoryPort.class));
    }
}