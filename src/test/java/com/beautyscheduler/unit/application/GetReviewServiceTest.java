package com.beautyscheduler.unit.application;

import com.beautyscheduler.application.port.out.ReviewRepositoryPort;
import com.beautyscheduler.application.usecase.review.GetReviewService;
import com.beautyscheduler.domain.entity.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetReviewService Unit Tests")
class GetReviewServiceTest {

    @Mock private ReviewRepositoryPort reviewRepository;

    private GetReviewService service;

    private UUID establishmentId;
    private UUID professionalId;
    private Review review;

    @BeforeEach
    void setUp() {
        service = new GetReviewService(reviewRepository);
        establishmentId = UUID.randomUUID();
        professionalId = UUID.randomUUID();
        review = new Review(UUID.randomUUID(), UUID.randomUUID(), establishmentId,
                professionalId, UUID.randomUUID(), 5, "Great!", LocalDateTime.now());
    }

    @Test
    @DisplayName("Should return reviews by establishment")
    void shouldFindByEstablishment() {
        when(reviewRepository.findByEstablishmentId(establishmentId)).thenReturn(List.of(review));

        List<Review> result = service.findByEstablishment(establishmentId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRating()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should return reviews by professional")
    void shouldFindByProfessional() {
        when(reviewRepository.findByProfessionalId(professionalId)).thenReturn(List.of(review));

        List<Review> result = service.findByProfessional(professionalId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getComment()).isEqualTo("Great!");
    }

    @Test
    @DisplayName("Should calculate average rating")
    void shouldCalculateAverageRating() {
        Review review2 = new Review(UUID.randomUUID(), UUID.randomUUID(), establishmentId,
                professionalId, UUID.randomUUID(), 3, "OK", LocalDateTime.now());
        when(reviewRepository.findByEstablishmentId(establishmentId)).thenReturn(List.of(review, review2));

        double avg = service.averageRatingForEstablishment(establishmentId);

        assertThat(avg).isEqualTo(4.0);
    }

    @Test
    @DisplayName("Should return 0.0 when no reviews exist")
    void shouldReturnZeroWhenNoReviews() {
        when(reviewRepository.findByEstablishmentId(establishmentId)).thenReturn(List.of());

        double avg = service.averageRatingForEstablishment(establishmentId);

        assertThat(avg).isEqualTo(0.0);
    }
}
