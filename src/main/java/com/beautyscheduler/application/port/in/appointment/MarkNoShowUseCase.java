package com.beautyscheduler.application.port.in.appointment;

import java.util.UUID;

public interface MarkNoShowUseCase {
    void execute(UUID appointmentId, UUID requesterId);
}
