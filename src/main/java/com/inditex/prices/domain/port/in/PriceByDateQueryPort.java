package com.inditex.prices.domain.port.in;

import com.inditex.prices.application.dto.PriceByDateRequest;
import com.inditex.prices.application.dto.PriceByDateResponse;

public interface PriceByDateQueryPort {
    PriceByDateResponse findApplicablePrice(PriceByDateRequest request);
}
