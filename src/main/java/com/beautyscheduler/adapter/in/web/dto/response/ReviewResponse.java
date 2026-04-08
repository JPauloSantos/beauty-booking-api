package com.beautyscheduler.adapter.in.web.dto.response;

import com.beautyscheduler.domain.entity.Review;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewResponse(
        UUID id,
        UUID clientId,
        UUID establishmentId,
        UUID professionalId,
        int rating,
        String comment,
        LocalDateTime createdAt
) {
    public static ReviewResponse from(Review r) {
        return new ReviewResponse(
                r.getId(), r.getClientId(), r.getEstablishmentId(),
                r.getProfessionalId(), r.getRating(), r.getComment(), r.getCreatedAt()
        );
    }
}
