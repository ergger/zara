package com.inditex.prices.adapter.in.rest.mapper;

import com.inditex.prices.adapter.in.rest.dto.*;
import com.inditex.prices.application.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PriceRestMapper {
    default LocalDateTime toLocalDateTime(ZonedDateTime zonedDateTime) {
        return zonedDateTime == null ? null : zonedDateTime.toLocalDateTime();
    }

    @Mapping(target = "applicationDate", expression = "java(toLocalDateTime(request.getApplicationDate()))")
    PriceByDateRequest toPriceByDateRequest(PriceByDateRestRequest request);

    PriceByDateRestResponse toPriceByDateRestResponse(PriceByDateResponse response);

    @Mapping(target = "startDate", expression = "java(toLocalDateTime(request.getStartDate()))")
    @Mapping(target = "endDate", expression = "java(toLocalDateTime(request.getEndDate()))")
    PriceUpsertRequest toPriceUpsertRequest(PriceUpsertRestRequest request);

    PriceUpsertRestResponse toPriceUpsertRestResponse(PriceUpsertResponse response);
}
