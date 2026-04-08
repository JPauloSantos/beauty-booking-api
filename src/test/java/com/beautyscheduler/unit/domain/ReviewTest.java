package com.beautyscheduler.unit.domain;

import com.beautyscheduler.domain.entity.Review;
import com.beautyscheduler.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Review Domain Tests")
class ReviewTest {

    @Test
    @DisplayName("Should create a valid review")
    void shouldCreateValidReview() {
        Review review = Review.create(UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), 5, "Excellent!");

        assertThat(review.getId()).isNotNull();
        assertThat(review.getRating()).isEqualTo(5);
        assertThat(review.getCreatedAt()).isNotNull();
    }

    @ParameterizedTest(name = "Rating {0} should be invalid")
    @ValueSource(ints = {0, 6, -1, 10})
    @DisplayName("Should throw when rating is out of range")
    void shouldThrowForInvalidRating(int invalidRating) {
        assertThatThrownBy(() -> Review.create(UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), invalidRating, "comment"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Rating must be between 1 and 5");
    }
}
