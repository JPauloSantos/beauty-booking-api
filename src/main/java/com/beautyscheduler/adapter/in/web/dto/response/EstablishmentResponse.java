package com.beautyscheduler.adapter.in.web.dto.response;

import com.beautyscheduler.domain.entity.Establishment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record EstablishmentResponse(
        UUID id,
        String name,
        String description,
        String fullAddress,
        String city,
        String state,
        List<String> photoUrls,
        UUID ownerId,
        boolean active,
        LocalDateTime createdAt
) {
    public static EstablishmentResponse from(Establishment e) {
        return new EstablishmentResponse(
                e.getId(),
                e.getName(),
                e.getDescription(),
                e.getAddress() != null ? e.getAddress().getFullAddress() : null,
                e.getAddress() != null ? e.getAddress().getCity() : null,
                e.getAddress() != null ? e.getAddress().getState() : null,
                e.getPhotoUrls(),
                e.getOwnerId(),
                e.isActive(),
                e.getCreatedAt()
        );
    }
}
