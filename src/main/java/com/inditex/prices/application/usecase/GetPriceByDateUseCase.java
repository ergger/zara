package com.inditex.prices.application.usecase;

import com.inditex.prices.application.dto.PriceByDateRequest;
import com.inditex.prices.application.dto.PriceByDateResponse;
import com.inditex.prices.application.mapper.PriceApplicationMapper;
import com.inditex.prices.domain.model.Price;
import com.inditex.prices.domain.port.in.PriceByDateQueryPort;
import com.inditex.prices.domain.port.out.PriceRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class GetPriceByDateUseCase implements PriceByDateQueryPort {

    private final PriceRepositoryPort priceRepositoryPort;
    private final PriceApplicationMapper mapper;

    public GetPriceByDateUseCase(PriceRepositoryPort priceRepositoryPort, PriceApplicationMapper mapper) {
        this.priceRepositoryPort = priceRepositoryPort;
        this.mapper = mapper;
    }

    @Override
    public PriceByDateResponse findApplicablePrice(PriceByDateRequest request) {
        Optional<Price> priceOpt = priceRepositoryPort.findApplicablePrice(
                request.getProductId(),
                request.getBrandId(),
                request.getApplicationDate()
        );
        Price price = priceOpt.orElseThrow(() -> new NoSuchElementException("No applicable price found"));
        return mapper.toPriceByDateResponse(price);
    }
}
