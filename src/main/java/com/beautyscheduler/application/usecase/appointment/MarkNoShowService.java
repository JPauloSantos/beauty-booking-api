package com.beautyscheduler.application.usecase.appointment;

import com.beautyscheduler.application.port.in.appointment.MarkNoShowUseCase;
import com.beautyscheduler.application.port.out.AppointmentRepositoryPort;
import com.beautyscheduler.application.port.out.UserRepositoryPort;
import com.beautyscheduler.domain.entity.Appointment;
import com.beautyscheduler.domain.entity.User;
import com.beautyscheduler.domain.exception.ResourceNotFoundException;
import com.beautyscheduler.domain.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MarkNoShowService implements MarkNoShowUseCase {

    private final AppointmentRepositoryPort appointmentRepository;
    private final UserRepositoryPort userRepository;

    public MarkNoShowService(AppointmentRepositoryPort appointmentRepository,
                              UserRepositoryPort userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void execute(UUID appointmentId, UUID requesterId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("User", requesterId));

        boolean isOwnerOrAdmin = requester.getRole() == User.UserRole.ESTABLISHMENT_OWNER
                || requester.getRole() == User.UserRole.ADMIN
                || requester.getRole() == User.UserRole.PROFESSIONAL;

        if (!isOwnerOrAdmin) {
            throw new UnauthorizedException("Only establishment staff can mark no-show.");
        }

        appointment.markNoShow();
        appointmentRepository.save(appointment);
    }
}
