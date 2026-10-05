package com.inditex.prices.domain.port.out;

import com.inditex.prices.domain.model.PriceChangedEvent;

public interface PriceChangedEventPublisher {
    void publish(PriceChangedEvent event);
}