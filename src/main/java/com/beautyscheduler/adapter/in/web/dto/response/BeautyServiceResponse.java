package com.beautyscheduler.adapter.in.web.dto.response;

import com.beautyscheduler.domain.entity.BeautyService;

import java.math.BigDecimal;
import java.util.UUID;

public record BeautyServiceResponse(
        UUID id,
        String name,
        String description,
        int durationMinutes,
        BigDecimal price,
        UUID establishmentId,
        boolean active
) {
    public static BeautyServiceResponse from(BeautyService s) {
        return new BeautyServiceResponse(
                s.getId(), s.getName(), s.getDescription(),
                s.getDurationMinutes(), s.getPrice(), s.getEstablishmentId(), s.isActive()
        );
    }
}
