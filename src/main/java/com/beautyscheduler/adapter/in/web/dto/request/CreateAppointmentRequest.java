package com.beautyscheduler.adapter.in.web.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateAppointmentRequest(
        @NotNull UUID professionalId,
        @NotNull UUID serviceId,
        @NotNull UUID establishmentId,
        @NotNull @Future(message = "Appointment must be in the future") LocalDateTime scheduledAt,
        String clientNotes
) {}
