package com.beautyscheduler.application.usecase.review;

import com.beautyscheduler.application.port.in.review.CreateReviewUseCase;
import com.beautyscheduler.application.port.out.AppointmentRepositoryPort;
import com.beautyscheduler.application.port.out.ReviewRepositoryPort;
import com.beautyscheduler.domain.entity.Appointment;
import com.beautyscheduler.domain.entity.Review;
import com.beautyscheduler.domain.exception.DomainException;
import com.beautyscheduler.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateReviewService implements CreateReviewUseCase {

    private final ReviewRepositoryPort reviewRepository;
    private final AppointmentRepositoryPort appointmentRepository;

    public CreateReviewService(ReviewRepositoryPort reviewRepository,
                                AppointmentRepositoryPort appointmentRepository) {
        this.reviewRepository = reviewRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    @Transactional
    public Review execute(Command command) {
        Appointment appointment = appointmentRepository.findById(command.appointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", command.appointmentId()));

        if (appointment.getStatus() != Appointment.AppointmentStatus.COMPLETED) {
            throw new DomainException("Reviews can only be left for completed appointments.");
        }

        if (!appointment.getClientId().equals(command.clientId())) {
            throw new DomainException("Only the client of this appointment can leave a review.");
        }

        if (reviewRepository.existsByClientIdAndAppointmentId(command.clientId(), command.appointmentId())) {
            throw new DomainException("A review already exists for this appointment.");
        }

        Review review = Review.create(
                command.clientId(),
                command.establishmentId(),
                command.professionalId(),
                command.appointmentId(),
                command.rating(),
                command.comment()
        );
        return reviewRepository.save(review);
    }
}
