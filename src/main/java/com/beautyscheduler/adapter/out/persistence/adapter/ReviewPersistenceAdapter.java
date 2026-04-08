package com.beautyscheduler.adapter.out.persistence.adapter;

import com.beautyscheduler.adapter.out.persistence.mapper.ReviewMapper;
import com.beautyscheduler.adapter.out.persistence.repository.ReviewJpaRepository;
import com.beautyscheduler.application.port.out.ReviewRepositoryPort;
import com.beautyscheduler.domain.entity.Review;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ReviewPersistenceAdapter implements ReviewRepositoryPort {

    private final ReviewJpaRepository repository;

    public ReviewPersistenceAdapter(ReviewJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Review save(Review review) {
        return ReviewMapper.toDomain(repository.save(ReviewMapper.toJpa(review)));
    }

    @Override
    public Optional<Review> findById(UUID id) {
        return repository.findById(id).map(ReviewMapper::toDomain);
    }

    @Override
    public List<Review> findByEstablishmentId(UUID establishmentId) {
        return repository.findByEstablishmentId(establishmentId).stream()
                .map(ReviewMapper::toDomain).toList();
    }

    @Override
    public List<Review> findByProfessionalId(UUID professionalId) {
        return repository.findByProfessionalId(professionalId).stream()
                .map(ReviewMapper::toDomain).toList();
    }

    @Override
    public boolean existsByClientIdAndAppointmentId(UUID clientId, UUID appointmentId) {
        return repository.existsByClientIdAndAppointmentId(clientId, appointmentId);
    }
}
