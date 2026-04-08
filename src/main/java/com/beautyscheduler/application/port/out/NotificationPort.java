package com.beautyscheduler.application.port.out;

import com.beautyscheduler.domain.entity.Appointment;

public interface NotificationPort {
    void sendAppointmentConfirmation(Appointment appointment, String clientEmail, String professionalEmail);
    void sendAppointmentReminder(Appointment appointment, String clientEmail, String professionalEmail);
    void sendAppointmentCancellation(Appointment appointment, String clientEmail, String professionalEmail);
}
