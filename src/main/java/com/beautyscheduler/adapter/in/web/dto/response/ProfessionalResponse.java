package com.beautyscheduler.adapter.in.web.dto.response;

import com.beautyscheduler.domain.entity.Professional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProfessionalResponse(
        UUID id,
        String name,
        String bio,
        List<String> specialties,
        UUID establishmentId,
        BigDecimal hourlyRate,
        boolean active
) {
    public static ProfessionalResponse from(Professional p) {
        return new ProfessionalResponse(
                p.getId(), p.getName(), p.getBio(), p.getSpecialties(),
                p.getEstablishmentId(), p.getHourlyRate(), p.isActive()
        );
    }
}
