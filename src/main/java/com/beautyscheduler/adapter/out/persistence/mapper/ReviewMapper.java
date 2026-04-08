package com.beautyscheduler.adapter.out.persistence.mapper;

import com.beautyscheduler.adapter.out.persistence.entity.ReviewJpaEntity;
import com.beautyscheduler.domain.entity.Review;

public class ReviewMapper {

    private ReviewMapper() {}

    public static Review toDomain(ReviewJpaEntity e) {
        return new Review(e.getId(), e.getClientId(), e.getEstablishmentId(),
                e.getProfessionalId(), e.getAppointmentId(), e.getRating(),
                e.getComment(), e.getCreatedAt());
    }

    public static ReviewJpaEntity toJpa(Review d) {
        ReviewJpaEntity e = new ReviewJpaEntity();
        e.setId(d.getId());
        e.setClientId(d.getClientId());
        e.setEstablishmentId(d.getEstablishmentId());
        e.setProfessionalId(d.getProfessionalId());
        e.setAppointmentId(d.getAppointmentId());
        e.setRating(d.getRating());
        e.setComment(d.getComment());
        e.setCreatedAt(d.getCreatedAt());
        return e;
    }
}
