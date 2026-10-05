package com.inditex.prices.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.inditex.prices.infrastructure.persistence.repository")
public class JpaConfig {
}
