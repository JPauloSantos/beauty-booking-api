package com.beautyscheduler.application.port.in.professional;

import com.beautyscheduler.domain.entity.Professional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CreateProfessionalUseCase {

    record Command(String name, String bio, List<String> specialties,
                   UUID establishmentId, UUID userId, BigDecimal hourlyRate) {}

    Professional execute(Command command);
}
