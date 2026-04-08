package com.beautyscheduler.adapter.in.web.dto.response;

import com.beautyscheduler.domain.entity.Appointment;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentResponse(
        UUID id,
        UUID clientId,
        UUID professionalId,
        UUID serviceId,
        UUID establishmentId,
        LocalDateTime scheduledAt,
        LocalDateTime endAt,
        String status,
        String clientNotes,
        LocalDateTime createdAt
) {
    public static AppointmentResponse from(Appointment a) {
        return new AppointmentResponse(
                a.getId(), a.getClientId(), a.getProfessionalId(), a.getServiceId(),
                a.getEstablishmentId(), a.getScheduledAt(), a.getEndAt(),
                a.getStatus().name(), a.getClientNotes(), a.getCreatedAt()
        );
    }
}
