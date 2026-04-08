package com.beautyscheduler.application.port.out;

import com.beautyscheduler.domain.entity.Appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepositoryPort {
    Appointment save(Appointment appointment);
    Optional<Appointment> findById(UUID id);
    List<Appointment> findByClientId(UUID clientId);
    List<Appointment> findByProfessionalId(UUID professionalId);
    List<Appointment> findByEstablishmentId(UUID establishmentId);
    List<Appointment> findByProfessionalIdAndDateRange(UUID professionalId,
                                                        LocalDateTime from, LocalDateTime to);
    List<Appointment> findConfirmedBefore(LocalDateTime dateTime);
}
