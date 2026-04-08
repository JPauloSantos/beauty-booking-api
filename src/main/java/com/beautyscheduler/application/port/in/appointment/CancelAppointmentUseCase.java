package com.beautyscheduler.application.port.in.appointment;

import java.util.UUID;

public interface CancelAppointmentUseCase {
    void execute(UUID appointmentId, UUID requesterId);
}
