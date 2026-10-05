package com.inditex.prices.adapter.in.rest.mapper;

import com.inditex.prices.adapter.in.rest.dto.PriceByDateRestRequest;
import com.inditex.prices.adapter.in.rest.dto.PriceByDateRestResponse;
import com.inditex.prices.adapter.in.rest.dto.PriceUpsertRestRequest;
import com.inditex.prices.adapter.in.rest.dto.PriceUpsertRestResponse;
import com.inditex.prices.application.dto.PriceByDateRequest;
import com.inditex.prices.application.dto.PriceByDateResponse;
import com.inditex.prices.application.dto.PriceUpsertRequest;
import com.inditex.prices.application.dto.PriceUpsertResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-05T19:33:03+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.12.1 (Ubuntu)"
)
@Component
public class PriceRestMapperImpl implements PriceRestMapper {

    @Override
    public PriceByDateRequest toPriceByDateRequest(PriceByDateRestRequest request) {
        if ( request == null ) {
            return null;
        }

        Integer brandId = null;
        Integer productId = null;

        brandId = request.getBrandId();
        productId = request.getProductId();

        LocalDateTime applicationDate = toLocalDateTime(request.getApplicationDate());

        PriceByDateRequest priceByDateRequest = new PriceByDateRequest( brandId, productId, applicationDate );

        return priceByDateRequest;
    }

    @Override
    public PriceByDateRestResponse toPriceByDateRestResponse(PriceByDateResponse response) {
        if ( response == null ) {
            return null;
        }

        PriceByDateRestResponse priceByDateRestResponse = new PriceByDateRestResponse();

        priceByDateRestResponse.setProductId( response.getProductId() );
        priceByDateRestResponse.setBrandId( response.getBrandId() );
        priceByDateRestResponse.setPriceList( response.getPriceList() );
        priceByDateRestResponse.setStartDate( response.getStartDate() );
        priceByDateRestResponse.setEndDate( response.getEndDate() );
        priceByDateRestResponse.setPrice( response.getPrice() );
        priceByDateRestResponse.setCurr( response.getCurr() );

        return priceByDateRestResponse;
    }

    @Override
    public PriceUpsertRequest toPriceUpsertRequest(PriceUpsertRestRequest request) {
        if ( request == null ) {
            return null;
        }

        Integer brandId = null;
        Integer productId = null;
        Integer priceList = null;
        Integer priority = null;
        BigDecimal price = null;
        String curr = null;

        brandId = request.getBrandId();
        productId = request.getProductId();
        priceList = request.getPriceList();
        priority = request.getPriority();
        price = request.getPrice();
        curr = request.getCurr();

        LocalDateTime startDate = toLocalDateTime(request.getStartDate());
        LocalDateTime endDate = toLocalDateTime(request.getEndDate());

        PriceUpsertRequest priceUpsertRequest = new PriceUpsertRequest( brandId, productId, priceList, startDate, endDate, priority, price, curr );

        return priceUpsertRequest;
    }

    @Override
    public PriceUpsertRestResponse toPriceUpsertRestResponse(PriceUpsertResponse response) {
        if ( response == null ) {
            return null;
        }

        PriceUpsertRestResponse priceUpsertRestResponse = new PriceUpsertRestResponse();

        priceUpsertRestResponse.setId( response.getId() );
        priceUpsertRestResponse.setBrandId( response.getBrandId() );
        priceUpsertRestResponse.setProductId( response.getProductId() );
        priceUpsertRestResponse.setPriceList( response.getPriceList() );
        priceUpsertRestResponse.setStartDate( response.getStartDate() );
        priceUpsertRestResponse.setEndDate( response.getEndDate() );
        priceUpsertRestResponse.setPriority( response.getPriority() );
        priceUpsertRestResponse.setPrice( response.getPrice() );
        priceUpsertRestResponse.setCurr( response.getCurr() );
        priceUpsertRestResponse.setCreated( response.isCreated() );
        priceUpsertRestResponse.setUpdated( response.isUpdated() );

        return priceUpsertRestResponse;
    }
}
