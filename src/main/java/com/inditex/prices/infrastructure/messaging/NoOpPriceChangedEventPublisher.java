package com.inditex.prices.infrastructure.messaging;

import com.inditex.prices.domain.model.PriceChangedEvent;
import com.inditex.prices.domain.port.out.PriceChangedEventPublisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "prices.messaging.kafka.enabled", havingValue = "false")
public class NoOpPriceChangedEventPublisher implements PriceChangedEventPublisher {

    @Override
    public void publish(PriceChangedEvent event) {
    }
}