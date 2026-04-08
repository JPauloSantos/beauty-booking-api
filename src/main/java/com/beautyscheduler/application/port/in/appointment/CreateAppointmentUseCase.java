package com.beautyscheduler.application.port.in.appointment;

import com.beautyscheduler.domain.entity.Appointment;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateAppointmentUseCase {

    record Command(UUID clientId, UUID professionalId, UUID serviceId,
                   UUID establishmentId, LocalDateTime scheduledAt, String clientNotes) {}

    Appointment execute(Command command);
}
