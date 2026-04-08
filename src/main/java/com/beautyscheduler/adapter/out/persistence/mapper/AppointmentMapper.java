package com.beautyscheduler.adapter.out.persistence.mapper;

import com.beautyscheduler.adapter.out.persistence.entity.AppointmentJpaEntity;
import com.beautyscheduler.domain.entity.Appointment;

public class AppointmentMapper {

    private AppointmentMapper() {}

    public static Appointment toDomain(AppointmentJpaEntity e) {
        return new Appointment(e.getId(), e.getClientId(), e.getProfessionalId(),
                e.getServiceId(), e.getEstablishmentId(), e.getScheduledAt(), e.getEndAt(),
                Appointment.AppointmentStatus.valueOf(e.getStatus().name()),
                e.getClientNotes(), e.getCreatedAt());
    }

    public static AppointmentJpaEntity toJpa(Appointment d) {
        AppointmentJpaEntity e = new AppointmentJpaEntity();
        e.setId(d.getId());
        e.setClientId(d.getClientId());
        e.setProfessionalId(d.getProfessionalId());
        e.setServiceId(d.getServiceId());
        e.setEstablishmentId(d.getEstablishmentId());
        e.setScheduledAt(d.getScheduledAt());
        e.setEndAt(d.getEndAt());
        e.setStatus(AppointmentJpaEntity.StatusEnum.valueOf(d.getStatus().name()));
        e.setClientNotes(d.getClientNotes());
        e.setCreatedAt(d.getCreatedAt());
        return e;
    }
}
