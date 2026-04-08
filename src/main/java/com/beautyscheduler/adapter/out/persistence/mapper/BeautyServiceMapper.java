package com.beautyscheduler.adapter.out.persistence.mapper;

import com.beautyscheduler.adapter.out.persistence.entity.BeautyServiceJpaEntity;
import com.beautyscheduler.domain.entity.BeautyService;

public class BeautyServiceMapper {

    private BeautyServiceMapper() {}

    public static BeautyService toDomain(BeautyServiceJpaEntity e) {
        return new BeautyService(e.getId(), e.getName(), e.getDescription(),
                e.getDurationMinutes(), e.getPrice(), e.getEstablishmentId(),
                e.isActive(), e.getCreatedAt());
    }

    public static BeautyServiceJpaEntity toJpa(BeautyService d) {
        BeautyServiceJpaEntity e = new BeautyServiceJpaEntity();
        e.setId(d.getId());
        e.setName(d.getName());
        e.setDescription(d.getDescription());
        e.setDurationMinutes(d.getDurationMinutes());
        e.setPrice(d.getPrice());
        e.setEstablishmentId(d.getEstablishmentId());
        e.setActive(d.isActive());
        e.setCreatedAt(d.getCreatedAt());
        return e;
    }
}
