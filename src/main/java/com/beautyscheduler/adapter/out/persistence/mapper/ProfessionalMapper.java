package com.beautyscheduler.adapter.out.persistence.mapper;

import com.beautyscheduler.adapter.out.persistence.entity.ProfessionalJpaEntity;
import com.beautyscheduler.domain.entity.Professional;

import java.util.ArrayList;

public class ProfessionalMapper {

    private ProfessionalMapper() {}

    public static Professional toDomain(ProfessionalJpaEntity e) {
        return new Professional(e.getId(), e.getName(), e.getBio(),
                new ArrayList<>(e.getSpecialties()), e.getEstablishmentId(),
                e.getUserId(), e.getHourlyRate(), e.isActive(), e.getCreatedAt());
    }

    public static ProfessionalJpaEntity toJpa(Professional d) {
        ProfessionalJpaEntity e = new ProfessionalJpaEntity();
        e.setId(d.getId());
        e.setName(d.getName());
        e.setBio(d.getBio());
        e.setSpecialties(new ArrayList<>(d.getSpecialties()));
        e.setEstablishmentId(d.getEstablishmentId());
        e.setUserId(d.getUserId());
        e.setHourlyRate(d.getHourlyRate());
        e.setActive(d.isActive());
        e.setCreatedAt(d.getCreatedAt());
        return e;
    }
}
