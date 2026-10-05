package com.inditex.prices.infrastructure.exception;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * El mensaje de error de un 400 no puede sugerir el formato que ha fallado.
 * Estos tests fijan esa garantia.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private MethodArgumentTypeMismatchException mismatch(String name, Object value, Class<?> type) {
        MethodParameter parameter = new MethodParameter(new Object() {
            public void sample(ZonedDateTime ignored) {
            }
        }.getClass().getDeclaredMethods()[0], 0);
        return new MethodArgumentTypeMismatchException(value, type, name, parameter, null);
    }

    private String messageOf(ResponseEntity<GlobalExceptionHandler.ErrorResponse> response) {
        return response.getBody().getMessage();
    }

    @Test
    void unencodedPlusIsExplainedInsteadOfRepeatingTheBrokenFormat() {
        // Lo que llega al servidor cuando el cliente envia "+02:00" sin codificar.
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleTypeMismatch(mismatch("applicationDate", "2020-06-14T10:00:00 02:00", ZonedDateTime.class));

        String message = messageOf(response);

        assertEquals(400, response.getStatusCode().value());
        assertTrue(message.contains("%2B"), "debe indicar como codificar el offset: " + message);
        assertTrue(message.contains("'Z'"), "debe ofrecer la alternativa con Z: " + message);
        assertTrue(message.contains("decoded as a space"),
                "debe explicar la causa real (el + se decodifica como espacio): " + message);
        assertTrue(!message.contains("Invalid value for parameter 'applicationDate': expected type ZonedDateTime in ISO-8601 date-time format (e.g. 2020-06-14T10:00:00+02:00)"),
                "no debe repetir el formato que falla");
    }

    @Test
    void genuinelyMalformedDateSuggestsValidExamplesOnly() {
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleTypeMismatch(mismatch("applicationDate", "14/06/2020", ZonedDateTime.class));

        String message = messageOf(response);

        assertTrue(message.contains("2020-06-14T10:00:00Z"), "debe sugerir Z: " + message);
        assertTrue(message.contains("%2B02:00"), "debe sugerir el offset codificado: " + message);
    }

    @Test
    void nonTemporalTypeKeepsTheTypeMismatchMessage() {
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleTypeMismatch(mismatch("brandId", "no-es-un-numero", Integer.class));

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid value for parameter 'brandId': expected type Integer", messageOf(response));
    }
}