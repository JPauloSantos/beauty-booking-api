package com.beautyscheduler.application.port.out;

import com.beautyscheduler.domain.entity.BeautyService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeautyServiceRepositoryPort {
    BeautyService save(BeautyService service);
    Optional<BeautyService> findById(UUID id);
    List<BeautyService> findByEstablishmentId(UUID establishmentId);
    void deleteById(UUID id);
}
