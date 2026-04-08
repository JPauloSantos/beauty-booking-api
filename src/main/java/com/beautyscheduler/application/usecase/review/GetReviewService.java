package com.beautyscheduler.application.usecase.review;

import com.beautyscheduler.application.port.in.review.GetReviewUseCase;
import com.beautyscheduler.application.port.out.ReviewRepositoryPort;
import com.beautyscheduler.domain.entity.Review;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GetReviewService implements GetReviewUseCase {

    private final ReviewRepositoryPort reviewRepository;

    public GetReviewService(ReviewRepositoryPort reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findByEstablishment(UUID establishmentId) {
        return reviewRepository.findByEstablishmentId(establishmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findByProfessional(UUID professionalId) {
        return reviewRepository.findByProfessionalId(professionalId);
    }

    @Override
    @Transactional(readOnly = true)
    public double averageRatingForEstablishment(UUID establishmentId) {
        List<Review> reviews = reviewRepository.findByEstablishmentId(establishmentId);
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }
}
