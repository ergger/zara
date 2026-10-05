package com.inditex.prices.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Prices API - Inditex")
                        .version("1.0.0")
                        .description("Microservicio de precios con Arquitectura Hexagonal + DDD. " +
                                "Fechas inclusivas. Instantes absolutos (TIMESTAMP) vs representación zonal. " +
                                "La zona horaria (offset) es obligatoria en todas las fechas; si se omite, la petición se rechaza con 400. " +
                                "No se aplica ninguna zona por defecto: el reloj de pared se toma del offset enviado. " +
                                "Desempate: PRIORITY DESC → START_DATE DESC ('más reciente') → clave única.")
                        .contact(new Contact()
                                .name("Inditex")
                                .email("info@inditex.com")));
    }
}
