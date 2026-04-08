package com.beautyscheduler.application.usecase.appointment;

import com.beautyscheduler.application.port.in.appointment.CancelAppointmentUseCase;
import com.beautyscheduler.application.port.out.AppointmentRepositoryPort;
import com.beautyscheduler.application.port.out.NotificationPort;
import com.beautyscheduler.application.port.out.UserRepositoryPort;
import com.beautyscheduler.domain.entity.Appointment;
import com.beautyscheduler.domain.entity.User;
import com.beautyscheduler.domain.exception.ResourceNotFoundException;
import com.beautyscheduler.domain.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CancelAppointmentService implements CancelAppointmentUseCase {

    private final AppointmentRepositoryPort appointmentRepository;
    private final UserRepositoryPort userRepository;
    private final NotificationPort notificationPort;

    public CancelAppointmentService(AppointmentRepositoryPort appointmentRepository,
                                     UserRepositoryPort userRepository,
                                     NotificationPort notificationPort) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.notificationPort = notificationPort;
    }

    @Override
    @Transactional
    public void execute(UUID appointmentId, UUID requesterId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("User", requesterId));

        boolean isClient = appointment.getClientId().equals(requesterId);
        boolean isOwnerOrAdmin = requester.getRole() == User.UserRole.ESTABLISHMENT_OWNER
                || requester.getRole() == User.UserRole.ADMIN;

        if (!isClient && !isOwnerOrAdmin) {
            throw new UnauthorizedException("You are not authorized to cancel this appointment.");
        }

        appointment.cancel();
        appointmentRepository.save(appointment);

        User client = userRepository.findById(appointment.getClientId()).orElse(null);
        User professional = userRepository.findById(appointment.getProfessionalId()).orElse(null);
        String clientEmail = client != null ? client.getEmail() : "";
        String professionalEmail = professional != null ? professional.getEmail() : "";
        notificationPort.sendAppointmentCancellation(appointment, clientEmail, professionalEmail);
    }
}
