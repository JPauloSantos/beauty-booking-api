package com.beautyscheduler.infrastructure.scheduler;

import com.beautyscheduler.application.port.out.AppointmentRepositoryPort;
import com.beautyscheduler.application.port.out.NotificationPort;
import com.beautyscheduler.application.port.out.UserRepositoryPort;
import com.beautyscheduler.domain.entity.Appointment;
import com.beautyscheduler.domain.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AppointmentReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(AppointmentReminderScheduler.class);

    private final AppointmentRepositoryPort appointmentRepository;
    private final UserRepositoryPort userRepository;
    private final NotificationPort notificationPort;

    public AppointmentReminderScheduler(AppointmentRepositoryPort appointmentRepository,
                                         UserRepositoryPort userRepository,
                                         NotificationPort notificationPort) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.notificationPort = notificationPort;
    }

    @Scheduled(cron = "0 0 8 * * *") // Every day at 8am
    public void sendReminders() {
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        List<Appointment> appointments = appointmentRepository.findConfirmedBefore(tomorrow);
        log.info("Sending reminders for {} appointments", appointments.size());

        appointments.forEach(appointment -> {
            User client = userRepository.findById(appointment.getClientId()).orElse(null);
            User professional = userRepository.findById(appointment.getProfessionalId()).orElse(null);
            String clientEmail = client != null ? client.getEmail() : "";
            String professionalEmail = professional != null ? professional.getEmail() : "";
            notificationPort.sendAppointmentReminder(appointment, clientEmail, professionalEmail);
        });
    }
}
