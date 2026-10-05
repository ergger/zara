package com.inditex.prices.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.NoSuchElementException;
import java.util.Set;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Set<Class<?>> TEMPORAL_TYPES = Set.of(
            ZonedDateTime.class,
            OffsetDateTime.class,
            LocalDateTime.class,
            LocalDate.class,
            LocalTime.class,
            Instant.class
    );

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return badRequest(invalidValueMessage(ex));
    }

    /**
     * En una query string un "+" sin codificar se decodifica como espacio, de modo
     * que "2020-06-14T10:00:00+02:00" llega al servidor como "2020-06-14T10:00:00 02:00".
     * El error que produce es muy dificil de diagnóstico si no se menciona, asi que
     * se detecta ese caso concreto y se proponen alternativas validas.
     */
    private String invalidValueMessage(MethodArgumentTypeMismatchException ex) {
        String name = ex.getName();
        String message = "Invalid value for parameter '" + name + "'";

        if (ex.getRequiredType() != null && TEMPORAL_TYPES.contains(ex.getRequiredType())) {
            String received = ex.getValue() == null ? "" : ex.getValue().toString();
            message += ": expected an ISO-8601 date-time";

            if (looksLikeUnencodedPlus(received)) {
                return message + " but received '" + received + "'. In a query string a literal '+' is decoded as a "
                        + "space. Encode it as %2B, or use the UTC designator 'Z'. "
                        + "Valid examples: 2020-06-14T10:00:00%2B02:00 or 2020-06-14T10:00:00Z";
            }

            return message + " such as 2020-06-14T10:00:00Z (UTC) or 2020-06-14T10:00:00%2B02:00 (offset encoded)";
        }

        return message + ": expected type " + (ex.getRequiredType() == null
                ? "unknown" : ex.getRequiredType().getSimpleName());
    }

    /**
     * Detecta el patron "fecha hora:espacio offset", que solo aparece cuando un '+'
     * sin codificar ha sido convertido en espacio por la decodificacion de la query.
     * El signo del offset ya no esta: el "+" es justo lo que se perdio.
     */
    private boolean looksLikeUnencodedPlus(String received) {
        return received.matches(".*\\d{2}:\\d{2}:\\d{2}\\s\\d{2}:?\\d{2}.*");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBodyValidation(MethodArgumentNotValidException ex) {
        return badRequest("Request body attributes do not match the expected request");
    }

    @ExceptionHandler({IllegalArgumentException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex) {
        return badRequest(ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> badRequest(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message));
    }

    public static class ErrorResponse {
        private int status;
        private String message;
        public ErrorResponse(int status, String message) {
            this.status = status;
            this.message = message;
        }
        public int getStatus() { return status; }
        public String getMessage() { return message; }
    }
}