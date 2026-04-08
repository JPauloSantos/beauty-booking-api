package com.beautyscheduler.application.port.out;

import com.beautyscheduler.domain.entity.Establishment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EstablishmentRepositoryPort {
    Establishment save(Establishment establishment);
    Optional<Establishment> findById(UUID id);
    List<Establishment> findByOwnerId(UUID ownerId);
    List<Establishment> search(String name, String city, String serviceName,
                                Double minRating, java.math.BigDecimal minPrice,
                                java.math.BigDecimal maxPrice);
    void deleteById(UUID id);
}
