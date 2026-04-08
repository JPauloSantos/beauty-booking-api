package com.beautyscheduler.application.port.out;

import com.beautyscheduler.domain.entity.Review;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepositoryPort {
    Review save(Review review);
    Optional<Review> findById(UUID id);
    List<Review> findByEstablishmentId(UUID establishmentId);
    List<Review> findByProfessionalId(UUID professionalId);
    boolean existsByClientIdAndAppointmentId(UUID clientId, UUID appointmentId);
}
