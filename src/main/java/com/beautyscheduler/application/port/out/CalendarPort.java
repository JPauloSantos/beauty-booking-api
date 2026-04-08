package com.beautyscheduler.application.port.out;

import com.beautyscheduler.domain.entity.Appointment;

public interface CalendarPort {
    String createEvent(Appointment appointment, String userEmail);
    void updateEvent(String eventId, Appointment appointment);
    void deleteEvent(String eventId);
}
