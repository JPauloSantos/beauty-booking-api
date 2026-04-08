package com.beautyscheduler.application.port.in.review;

import com.beautyscheduler.domain.entity.Review;

import java.util.UUID;

public interface CreateReviewUseCase {

    record Command(UUID clientId, UUID establishmentId, UUID professionalId,
                   UUID appointmentId, int rating, String comment) {}

    Review execute(Command command);
}
