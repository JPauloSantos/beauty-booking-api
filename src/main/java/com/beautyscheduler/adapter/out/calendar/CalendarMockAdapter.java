package com.beautyscheduler.adapter.out.calendar;

import com.beautyscheduler.application.port.out.CalendarPort;
import com.beautyscheduler.domain.entity.Appointment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mock implementation of CalendarPort.
 * In production, this would be replaced with Google Calendar or Outlook integration.
 */
@Component
public class CalendarMockAdapter implements CalendarPort {

    private static final Logger log = LoggerFactory.getLogger(CalendarMockAdapter.class);

    @Override
    public String createEvent(Appointment appointment, String userEmail) {
        String eventId = "mock-event-" + UUID.randomUUID();
        log.info("[CALENDAR MOCK] Created event {} for appointment {} for user {}",
                eventId, appointment.getId(), userEmail);
        return eventId;
    }

    @Override
    public void updateEvent(String eventId, Appointment appointment) {
        log.info("[CALENDAR MOCK] Updated event {} for appointment {}", eventId, appointment.getId());
    }

    @Override
    public void deleteEvent(String eventId) {
        log.info("[CALENDAR MOCK] Deleted event {}", eventId);
    }
}
