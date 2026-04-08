package com.beautyscheduler.application.port.in.review;

import com.beautyscheduler.domain.entity.Review;

import java.util.List;
import java.util.UUID;

public interface GetReviewUseCase {

    List<Review> findByEstablishment(UUID establishmentId);

    List<Review> findByProfessional(UUID professionalId);

    double averageRatingForEstablishment(UUID establishmentId);
}
