package com.inditex.prices.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inditex.prices.domain.model.Money;
import com.inditex.prices.domain.model.Price;
import com.inditex.prices.domain.model.PriceChangedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaPriceChangedEventPublisherTest {
    private static final String TOPIC = "prices.price-changed.v1";
    private static final LocalDateTime START = LocalDateTime.of(2020, 6, 14, 0, 0);
    private static final LocalDateTime END = LocalDateTime.of(2020, 12, 31, 23, 59, 59);

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private KafkaPriceChangedEventPublisher publisher() {
        return new KafkaPriceChangedEventPublisher(kafkaTemplate, objectMapper, TOPIC);
    }

    private static PriceChangedEvent event() {
        Price price = new Price(7L, 1, 35455, 2, START, END, 0, new Money(new BigDecimal("41.75"), "EUR"));
        return PriceChangedEvent.created(price);
    }

    @SuppressWarnings("unchecked")
    private String capturedPayload() {
        ArgumentCaptor<String> payload = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq(TOPIC), anyString(), payload.capture());
        return payload.getValue();
    }

    @Test
    void shouldSendJsonToTheConfiguredTopicKeyedByProduct() {
        when(kafkaTemplate.send(anyString(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        publisher().publish(event());

        verify(kafkaTemplate).send(eq(TOPIC), eq("35455"), anyString());
        String json = capturedPayload();
        assertTrue(json.contains("\"changeType\":\"CREATED\""));
        assertTrue(json.contains("\"priceId\":7"));
        assertTrue(json.contains("\"productId\":35455"));
        assertTrue(json.contains("\"brandId\":1"));
        assertTrue(json.contains("\"priceList\":2"));
        assertTrue(json.contains("\"price\":{\"amount\":41.75,\"currency\":\"EUR\"}"));
    }

    @Test
    void shouldSerializeDatesInIsoFormat() {
        when(kafkaTemplate.send(anyString(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        publisher().publish(event());

        String json = capturedPayload();
        assertTrue(json.contains("\"startDate\":\"2020-06-14T00:00:00\""));
        assertTrue(json.contains("\"endDate\":\"2020-12-31T23:59:59\""));
    }

    @Test
    void shouldSwallowBrokerFailureWithoutThrowing() {
        CompletableFuture<SendResult<String, String>> failed = new CompletableFuture<>();
        failed.completeExceptionally(new IllegalStateException("broker down"));
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(failed);

        assertDoesNotThrow(() -> publisher().publish(event()));

        verify(kafkaTemplate).send(eq(TOPIC), eq("35455"), anyString());
    }

    @Test
    void shouldSwallowSynchronousSendFailureWithoutThrowing() {
        when(kafkaTemplate.send(anyString(), anyString(), anyString()))
                .thenThrow(new IllegalStateException("producer closed"));

        assertDoesNotThrow(() -> publisher().publish(event()));
    }

    @Test
    void shouldSwallowSerializationFailureWithoutSending() throws Exception {
        ObjectMapper broken = mock(ObjectMapper.class);
        when(broken.writeValueAsString(any())).thenThrow(new IllegalArgumentException("boom"));

        assertDoesNotThrow(() -> new KafkaPriceChangedEventPublisher(kafkaTemplate, broken, TOPIC).publish(event()));

        verify(kafkaTemplate, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    void noOpPublisherSwallowsTheEvent() {
        assertDoesNotThrow(() -> new NoOpPriceChangedEventPublisher().publish(event()));
    }
}