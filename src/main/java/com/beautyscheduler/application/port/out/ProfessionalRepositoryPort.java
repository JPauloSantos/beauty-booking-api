package com.beautyscheduler.application.port.out;

import com.beautyscheduler.domain.entity.Professional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfessionalRepositoryPort {
    Professional save(Professional professional);
    Optional<Professional> findById(UUID id);
    List<Professional> findByEstablishmentId(UUID establishmentId);
    void deleteById(UUID id);
}
