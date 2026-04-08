package com.beautyscheduler.application.usecase.appointment;

import com.beautyscheduler.application.port.in.appointment.RescheduleAppointmentUseCase;
import com.beautyscheduler.application.port.out.AppointmentRepositoryPort;
import com.beautyscheduler.application.port.out.BeautyServiceRepositoryPort;
import com.beautyscheduler.domain.entity.Appointment;
import com.beautyscheduler.domain.entity.BeautyService;
import com.beautyscheduler.domain.exception.ResourceNotFoundException;
import com.beautyscheduler.domain.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RescheduleAppointmentService implements RescheduleAppointmentUseCase {

    private final AppointmentRepositoryPort appointmentRepository;
    private final BeautyServiceRepositoryPort beautyServiceRepository;

    public RescheduleAppointmentService(AppointmentRepositoryPort appointmentRepository,
                                         BeautyServiceRepositoryPort beautyServiceRepository) {
        this.appointmentRepository = appointmentRepository;
        this.beautyServiceRepository = beautyServiceRepository;
    }

    @Override
    @Transactional
    public Appointment execute(Command command) {
        Appointment appointment = appointmentRepository.findById(command.appointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", command.appointmentId()));

        if (!appointment.getClientId().equals(command.requesterId())) {
            throw new UnauthorizedException("Only the client can reschedule this appointment.");
        }

        BeautyService service = beautyServiceRepository.findById(appointment.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("BeautyService", appointment.getServiceId()));

        appointment.reschedule(command.newScheduledAt(), service.getDurationMinutes());
        return appointmentRepository.save(appointment);
    }
}
