package com.inditex.prices.domain.port.in;

import com.inditex.prices.application.dto.PriceUpsertRequest;
import com.inditex.prices.application.dto.PriceUpsertResponse;

public interface PriceUpsertPort {
    PriceUpsertResponse upsertPrice(PriceUpsertRequest request);
}
