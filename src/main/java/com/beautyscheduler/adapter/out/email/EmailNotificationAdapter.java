package com.beautyscheduler.adapter.out.email;

import com.beautyscheduler.application.port.out.NotificationPort;
import com.beautyscheduler.domain.entity.Appointment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class EmailNotificationAdapter implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationAdapter.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    private final JavaMailSender mailSender;

    public EmailNotificationAdapter(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendAppointmentConfirmation(Appointment appointment,
                                             String clientEmail, String professionalEmail) {
        String subject = "Agendamento Confirmado - Beauty Scheduler";
        String body = String.format(
                "Seu agendamento foi confirmado para %s.%nID: %s",
                appointment.getScheduledAt().format(FORMATTER), appointment.getId()
        );
        sendEmail(clientEmail, subject, body);
        sendEmail(professionalEmail, subject, body);
    }

    @Override
    public void sendAppointmentReminder(Appointment appointment,
                                         String clientEmail, String professionalEmail) {
        String subject = "Lembrete de Agendamento - Beauty Scheduler";
        String body = String.format(
                "Lembrete: você tem um agendamento amanhã às %s.",
                appointment.getScheduledAt().format(FORMATTER)
        );
        sendEmail(clientEmail, subject, body);
        sendEmail(professionalEmail, subject, body);
    }

    @Override
    public void sendAppointmentCancellation(Appointment appointment,
                                             String clientEmail, String professionalEmail) {
        String subject = "Agendamento Cancelado - Beauty Scheduler";
        String body = String.format(
                "O agendamento de %s foi cancelado.",
                appointment.getScheduledAt().format(FORMATTER)
        );
        sendEmail(clientEmail, subject, body);
        sendEmail(professionalEmail, subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        if (to == null || to.isBlank()) return;
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
