package com.beautyscheduler.unit.application;

import com.beautyscheduler.application.port.in.review.CreateReviewUseCase;
import com.beautyscheduler.application.port.out.AppointmentRepositoryPort;
import com.beautyscheduler.application.port.out.ReviewRepositoryPort;
import com.beautyscheduler.application.usecase.review.CreateReviewService;
import com.beautyscheduler.domain.entity.Appointment;
import com.beautyscheduler.domain.entity.Review;
import com.beautyscheduler.domain.exception.DomainException;
import com.beautyscheduler.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateReviewService Unit Tests")
class CreateReviewServiceTest {

    @Mock private ReviewRepositoryPort reviewRepository;
    @Mock private AppointmentRepositoryPort appointmentRepository;

    private CreateReviewService service;

    private UUID clientId;
    private UUID appointmentId;
    private UUID establishmentId;
    private UUID professionalId;
    private Appointment completedAppointment;

    @BeforeEach
    void setUp() {
        service = new CreateReviewService(reviewRepository, appointmentRepository);

        clientId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();
        establishmentId = UUID.randomUUID();
        professionalId = UUID.randomUUID();

        completedAppointment = new Appointment(
                appointmentId, clientId, professionalId, UUID.randomUUID(), establishmentId,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1).plusHours(1),
                Appointment.AppointmentStatus.COMPLETED, null, LocalDateTime.now().minusDays(1)
        );
    }

    @Test
    @DisplayName("Should create review successfully for a completed appointment")
    void shouldCreateReviewSuccessfully() {
        CreateReviewUseCase.Command command = new CreateReviewUseCase.Command(
                clientId, establishmentId, professionalId, appointmentId, 5, "Excellent service!");

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(completedAppointment));
        when(reviewRepository.existsByClientIdAndAppointmentId(clientId, appointmentId)).thenReturn(false);
        when(reviewRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Review result = service.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getRating()).isEqualTo(5);
        assertThat(result.getComment()).isEqualTo("Excellent service!");
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    @DisplayName("Should throw DomainException when appointment is not completed")
    void shouldThrowWhenAppointmentNotCompleted() {
        Appointment pendingAppointment = new Appointment(
                appointmentId, clientId, professionalId, UUID.randomUUID(), establishmentId,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1),
                Appointment.AppointmentStatus.PENDING, null, LocalDateTime.now()
        );

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(pendingAppointment));

        CreateReviewUseCase.Command command = new CreateReviewUseCase.Command(
                clientId, establishmentId, professionalId, appointmentId, 4, "Good");

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("completed");
    }

    @Test
    @DisplayName("Should throw DomainException when reviewer is not the appointment client")
    void shouldThrowWhenReviewerIsNotClient() {
        UUID anotherClientId = UUID.randomUUID();
        CreateReviewUseCase.Command command = new CreateReviewUseCase.Command(
                anotherClientId, establishmentId, professionalId, appointmentId, 3, "OK");

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(completedAppointment));

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("client");
    }

    @Test
    @DisplayName("Should throw DomainException when review already exists")
    void shouldThrowWhenReviewAlreadyExists() {
        CreateReviewUseCase.Command command = new CreateReviewUseCase.Command(
                clientId, establishmentId, professionalId, appointmentId, 4, "Nice");

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(completedAppointment));
        when(reviewRepository.existsByClientIdAndAppointmentId(clientId, appointmentId)).thenReturn(true);

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("review already exists");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when appointment not found")
    void shouldThrowWhenAppointmentNotFound() {
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        CreateReviewUseCase.Command command = new CreateReviewUseCase.Command(
                clientId, establishmentId, professionalId, appointmentId, 5, "Great");

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
