package com.beautyscheduler.application.port.in.establishment;

import com.beautyscheduler.domain.entity.Establishment;

import java.math.BigDecimal;
import java.util.List;

public interface SearchEstablishmentUseCase {

    record Filters(
            String name,
            String city,
            String serviceName,
            Double minRating,
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {}

    List<Establishment> search(Filters filters);
}
