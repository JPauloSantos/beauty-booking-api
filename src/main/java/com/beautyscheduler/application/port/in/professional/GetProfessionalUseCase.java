package com.beautyscheduler.application.port.in.professional;

import com.beautyscheduler.domain.entity.Professional;

import java.util.List;
import java.util.UUID;

public interface GetProfessionalUseCase {

    Professional findById(UUID id);

    List<Professional> findByEstablishment(UUID establishmentId);
}
