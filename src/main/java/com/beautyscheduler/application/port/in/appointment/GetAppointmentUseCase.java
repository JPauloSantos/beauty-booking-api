package com.beautyscheduler.application.port.in.appointment;

import com.beautyscheduler.domain.entity.Appointment;

import java.util.List;
import java.util.UUID;

public interface GetAppointmentUseCase {

    Appointment findById(UUID id);

    List<Appointment> findByClient(UUID clientId);

    List<Appointment> findByProfessional(UUID professionalId);

    List<Appointment> findByEstablishment(UUID establishmentId);
}
