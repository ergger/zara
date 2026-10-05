package com.inditex.prices.infrastructure.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
public class TimeZoneStartupLogger {

    private static final Logger log = LoggerFactory.getLogger(TimeZoneStartupLogger.class);

    @PostConstruct
    void logResolvedTimeZone() {
        log.info("Zona horaria efectiva de la JVM: {} (offset {}). Los timestamps de los logs van en esa zona.",
                ZoneId.systemDefault(), ZoneId.systemDefault().getRules().getOffset(java.time.Instant.now()));
    }
}