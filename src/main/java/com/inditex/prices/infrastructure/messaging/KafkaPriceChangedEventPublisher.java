package com.inditex.prices.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inditex.prices.domain.model.PriceChangedEvent;
import com.inditex.prices.domain.port.out.PriceChangedEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "prices.messaging.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaPriceChangedEventPublisher implements PriceChangedEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaPriceChangedEventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;

    public KafkaPriceChangedEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                           ObjectMapper objectMapper,
                                           @Value("${prices.messaging.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topic = topic;
    }

    @Override
    public void publish(PriceChangedEvent event) {
        String key = String.valueOf(event.productId());
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, key, payload).whenComplete((result, error) -> {
                if (error != null) {
                    log.error("No se pudo publicar PriceChanged {} en el topic {} para productId={}: {}",
                            event.changeType(), topic, event.productId(), error.getMessage());
                } else {
                    log.debug("PriceChanged {} publicado en {} para productId={}",
                            event.changeType(), topic, event.productId());
                }
            });
        } catch (Exception e) {
            log.error("No se pudo emitir PriceChanged {} para productId={}", event.changeType(), event.productId(), e);
        }
    }
}