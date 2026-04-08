package com.beautyscheduler.application.port.in.appointment;

import com.beautyscheduler.domain.entity.Appointment;

import java.time.LocalDateTime;
import java.util.UUID;

public interface RescheduleAppointmentUseCase {

    record Command(UUID appointmentId, UUID requesterId, LocalDateTime newScheduledAt) {}

    Appointment execute(Command command);
}
