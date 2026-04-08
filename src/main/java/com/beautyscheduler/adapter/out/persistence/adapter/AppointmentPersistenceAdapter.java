package com.beautyscheduler.adapter.out.persistence.adapter;

import com.beautyscheduler.adapter.out.persistence.mapper.AppointmentMapper;
import com.beautyscheduler.adapter.out.persistence.repository.AppointmentJpaRepository;
import com.beautyscheduler.application.port.out.AppointmentRepositoryPort;
import com.beautyscheduler.domain.entity.Appointment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AppointmentPersistenceAdapter implements AppointmentRepositoryPort {

    private final AppointmentJpaRepository repository;

    public AppointmentPersistenceAdapter(AppointmentJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Appointment save(Appointment appointment) {
        return AppointmentMapper.toDomain(repository.save(AppointmentMapper.toJpa(appointment)));
    }

    @Override
    public Optional<Appointment> findById(UUID id) {
        return repository.findById(id).map(AppointmentMapper::toDomain);
    }

    @Override
    public List<Appointment> findByClientId(UUID clientId) {
        return repository.findByClientId(clientId).stream().map(AppointmentMapper::toDomain).toList();
    }

    @Override
    public List<Appointment> findByProfessionalId(UUID professionalId) {
        return repository.findByProfessionalId(professionalId).stream().map(AppointmentMapper::toDomain).toList();
    }

    @Override
    public List<Appointment> findByEstablishmentId(UUID establishmentId) {
        return repository.findByEstablishmentId(establishmentId).stream().map(AppointmentMapper::toDomain).toList();
    }

    @Override
    public List<Appointment> findByProfessionalIdAndDateRange(UUID professionalId,
                                                               LocalDateTime from, LocalDateTime to) {
        return repository.findByProfessionalIdAndDateRange(professionalId, from, to)
                .stream().map(AppointmentMapper::toDomain).toList();
    }

    @Override
    public List<Appointment> findConfirmedBefore(LocalDateTime dateTime) {
        return repository.findConfirmedBefore(dateTime).stream().map(AppointmentMapper::toDomain).toList();
    }
}
