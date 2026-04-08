package com.beautyscheduler.application.usecase.appointment;

import com.beautyscheduler.application.port.in.appointment.CreateAppointmentUseCase;
import com.beautyscheduler.application.port.out.*;
import com.beautyscheduler.domain.entity.Appointment;
import com.beautyscheduler.domain.entity.BeautyService;
import com.beautyscheduler.domain.entity.User;
import com.beautyscheduler.domain.exception.AppointmentConflictException;
import com.beautyscheduler.domain.exception.DomainException;
import com.beautyscheduler.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CreateAppointmentService implements CreateAppointmentUseCase {

    private final AppointmentRepositoryPort appointmentRepository;
    private final BeautyServiceRepositoryPort beautyServiceRepository;
    private final UserRepositoryPort userRepository;
    private final ProfessionalRepositoryPort professionalRepository;
    private final NotificationPort notificationPort;

    public CreateAppointmentService(AppointmentRepositoryPort appointmentRepository,
                                     BeautyServiceRepositoryPort beautyServiceRepository,
                                     UserRepositoryPort userRepository,
                                     ProfessionalRepositoryPort professionalRepository,
                                     NotificationPort notificationPort) {
        this.appointmentRepository = appointmentRepository;
        this.beautyServiceRepository = beautyServiceRepository;
        this.userRepository = userRepository;
        this.professionalRepository = professionalRepository;
        this.notificationPort = notificationPort;
    }

    @Override
    @Transactional
    public Appointment execute(Command command) {
        BeautyService service = beautyServiceRepository.findById(command.serviceId())
                .orElseThrow(() -> new ResourceNotFoundException("BeautyService", command.serviceId()));

        professionalRepository.findById(command.professionalId())
                .orElseThrow(() -> new ResourceNotFoundException("Professional", command.professionalId()));

        User client = userRepository.findById(command.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("User", command.clientId()));

        checkForConflicts(command, service.getDurationMinutes());

        Appointment appointment = Appointment.create(
                command.clientId(),
                command.professionalId(),
                command.serviceId(),
                command.establishmentId(),
                command.scheduledAt(),
                service.getDurationMinutes(),
                command.clientNotes()
        );

        Appointment saved = appointmentRepository.save(appointment);

        User professional = userRepository.findById(command.professionalId()).orElse(null);
        String professionalEmail = professional != null ? professional.getEmail() : "";
        notificationPort.sendAppointmentConfirmation(saved, client.getEmail(), professionalEmail);

        return saved;
    }

    private void checkForConflicts(Command command, int durationMinutes) {
        LocalDateTime endAt = command.scheduledAt().plusMinutes(durationMinutes);
        List<Appointment> existing = appointmentRepository.findByProfessionalIdAndDateRange(
                command.professionalId(), command.scheduledAt().minusHours(4), endAt.plusHours(4));

        Appointment newAppointment = new Appointment();
        newAppointment.setScheduledAt(command.scheduledAt());
        newAppointment.setEndAt(endAt);
        newAppointment.setProfessionalId(command.professionalId());
        newAppointment.setStatus(Appointment.AppointmentStatus.PENDING);

        boolean hasConflict = existing.stream()
                .filter(a -> a.getStatus() != Appointment.AppointmentStatus.CANCELLED
                          && a.getStatus() != Appointment.AppointmentStatus.NO_SHOW)
                .anyMatch(a -> newAppointment.conflictsWith(a));

        if (hasConflict) {
            throw new AppointmentConflictException(
                    "Professional already has an appointment at the requested time.");
        }
    }
}
