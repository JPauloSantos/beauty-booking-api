package com.beautyscheduler.application.usecase.appointment;

import com.beautyscheduler.application.port.in.appointment.GetAppointmentUseCase;
import com.beautyscheduler.application.port.out.AppointmentRepositoryPort;
import com.beautyscheduler.domain.entity.Appointment;
import com.beautyscheduler.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GetAppointmentService implements GetAppointmentUseCase {

    private final AppointmentRepositoryPort appointmentRepository;

    public GetAppointmentService(AppointmentRepositoryPort appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Appointment findById(UUID id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> findByClient(UUID clientId) {
        return appointmentRepository.findByClientId(clientId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> findByProfessional(UUID professionalId) {
        return appointmentRepository.findByProfessionalId(professionalId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> findByEstablishment(UUID establishmentId) {
        return appointmentRepository.findByEstablishmentId(establishmentId);
    }
}
