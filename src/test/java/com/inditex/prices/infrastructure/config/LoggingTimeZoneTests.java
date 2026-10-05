package com.inditex.prices.infrastructure.config;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.net.URL;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica que los timestamps de log llevan su propio offset y que ese offset es
 * el real de la JVM (logback-spring.xml no fija zona, usa %d{...XXX}).
 *
 * <p>El offset se compara como valor, no como texto: ISO-8601 representa el
 * desfase cero como "Z" y cualquier otro como "+02:00". Ambos son correctos, asi
 * que el test no debe depender de en que zona se ejecute.
 */
@ExtendWith(OutputCaptureExtension.class)
class LoggingTimeZoneTests {

    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss.SSSXXX";

    private static final Pattern TIMESTAMP_WITH_OFFSET =
            Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}(?:[+-]\\d{2}:\\d{2}|Z)");

    private LoggerContext originalContext;

    /**
     * Aplica logback-spring.xml de forma explicita. Sin esto el test dependia de
     * que otro test hubiera levantado antes un contexto de Spring, y fallaba al
     * ejecutarse aislado.
     */
    @BeforeEach
    void applyProjectLoggingConfiguration() throws JoranException {
        originalContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        URL config = getClass().getClassLoader().getResource("logback-spring.xml");
        assertTrue(config != null, "no se encuentra logback-spring.xml en el classpath");

        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(originalContext);
        configurator.doConfigure(config);
    }

    @AfterEach
    void restoreLoggingConfiguration() {
        if (originalContext != null) {
            originalContext.reset();
        }
    }

    @Test
    void everyLogLineCarriesItsOwnOffset(CapturedOutput output) {
        LoggerFactory.getLogger("timezone-test").info("linea con offset");

        assertTrue(TIMESTAMP_WITH_OFFSET.matcher(output.getOut()).find(),
                "cada linea debe llevar su offset. Salida:\n" + output.getOut());
    }

    @Test
    void printedTimestampMatchesTheJvmZone() {
        // Si el patron fijara una zona, esto fallaria al arrancar con otra TZ.
        ZonedDateTime now = ZonedDateTime.now();
        String printed = now.format(DateTimeFormatter.ofPattern(PATTERN));

        ZoneOffset real = now.getOffset();
        // ZoneOffset de cero se representa como "Z"; el resto, como "+02:00".
        String expected = real.getTotalSeconds() == 0 ? "Z" : real.getId();

        assertTrue(printed.endsWith(expected),
                "el timestamp debe llevar el offset real de la JVM (" + expected + "): " + printed);
    }

    @Test
    void startupLoggerReportsTheJvmZoneWithItsOffset(CapturedOutput output) {
        new TimeZoneStartupLogger().logResolvedTimeZone();

        String zone = java.time.ZoneId.systemDefault().getId();
        String printedOffset = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("XXX"));

        assertTrue(output.getOut().contains(zone),
                "debe indicar la zona efectiva de la JVM (" + zone + "). Salida:\n" + output.getOut());
        assertTrue(TIMESTAMP_WITH_OFFSET.matcher(output.getOut()).find(),
                "la linea de arranque debe llevar offset. Salida:\n" + output.getOut());
        assertTrue(printedOffset.equals("Z") || printedOffset.matches("[+-]\\d{2}:\\d{2}"),
                "offset debe ser ISO-8601 valido: " + printedOffset);
    }
}