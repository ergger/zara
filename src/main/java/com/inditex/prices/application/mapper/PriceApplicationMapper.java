package com.inditex.prices.application.mapper;

import com.inditex.prices.application.dto.PriceByDateResponse;
import com.inditex.prices.application.dto.PriceUpsertRequest;
import com.inditex.prices.application.dto.PriceUpsertResponse;
import com.inditex.prices.domain.model.Money;
import com.inditex.prices.domain.model.Price;
import org.springframework.stereotype.Component;

@Component
public class PriceApplicationMapper {

    public PriceByDateResponse toPriceByDateResponse(Price price) {
        if (price == null) return null;
        return new PriceByDateResponse(
                price.productId(),
                price.brandId(),
                price.priceList(),
                price.startDate(),
                price.endDate(),
                price.money() != null ? price.money().amount() : null,
                price.money() != null ? price.money().currency() : null
        );
    }

    public Price toPrice(PriceUpsertRequest request) {
        if (request == null) return null;
        return new Price(
                request.getBrandId(),
                request.getProductId(),
                request.getPriceList(),
                request.getStartDate(),
                request.getEndDate(),
                request.getPriority(),
                new Money(request.getPrice(), request.getCurr())
        );
    }

    public PriceUpsertResponse toPriceUpsertResponse(Price price, boolean created, boolean updated) {
        if (price == null) return null;
        return new PriceUpsertResponse(
                price.id(),
                price.brandId(),
                price.productId(),
                price.priceList(),
                price.startDate(),
                price.endDate(),
                price.priority(),
                price.money() != null ? price.money().amount() : null,
                price.money() != null ? price.money().currency() : null,
                created,
                updated
        );
    }
}
