package com.beautyscheduler.unit.application;

import com.beautyscheduler.application.port.in.appointment.RescheduleAppointmentUseCase;
import com.beautyscheduler.application.port.out.AppointmentRepositoryPort;
import com.beautyscheduler.application.port.out.BeautyServiceRepositoryPort;
import com.beautyscheduler.application.usecase.appointment.RescheduleAppointmentService;
import com.beautyscheduler.domain.entity.Appointment;
import com.beautyscheduler.domain.entity.BeautyService;
import com.beautyscheduler.domain.exception.ResourceNotFoundException;
import com.beautyscheduler.domain.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RescheduleAppointmentService Unit Tests")
class RescheduleAppointmentServiceTest {

    @Mock private AppointmentRepositoryPort appointmentRepository;
    @Mock private BeautyServiceRepositoryPort beautyServiceRepository;

    private RescheduleAppointmentService service;

    private UUID clientId;
    private UUID appointmentId;
    private UUID serviceId;
    private Appointment appointment;
    private BeautyService beautyService;

    @BeforeEach
    void setUp() {
        service = new RescheduleAppointmentService(appointmentRepository, beautyServiceRepository);

        clientId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();
        serviceId = UUID.randomUUID();

        appointment = new Appointment(
                appointmentId, clientId, UUID.randomUUID(), serviceId, UUID.randomUUID(),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1),
                Appointment.AppointmentStatus.CONFIRMED, null, LocalDateTime.now()
        );

        beautyService = new BeautyService(serviceId, "Haircut", "desc",
                60, BigDecimal.valueOf(50), UUID.randomUUID(), true, LocalDateTime.now());
    }

    @Test
    @DisplayName("Should reschedule appointment successfully")
    void shouldRescheduleSuccessfully() {
        LocalDateTime newTime = LocalDateTime.now().plusDays(3);
        RescheduleAppointmentUseCase.Command command =
                new RescheduleAppointmentUseCase.Command(appointmentId, clientId, newTime);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(beautyServiceRepository.findById(serviceId)).thenReturn(Optional.of(beautyService));
        when(appointmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Appointment result = service.execute(command);

        assertThat(result.getScheduledAt()).isEqualTo(newTime);
        assertThat(result.getStatus()).isEqualTo(Appointment.AppointmentStatus.PENDING);
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when requester is not the client")
    void shouldThrowWhenRequesterIsNotClient() {
        UUID strangerID = UUID.randomUUID();
        RescheduleAppointmentUseCase.Command command =
                new RescheduleAppointmentUseCase.Command(appointmentId, strangerID, LocalDateTime.now().plusDays(3));

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when appointment not found")
    void shouldThrowWhenAppointmentNotFound() {
        RescheduleAppointmentUseCase.Command command =
                new RescheduleAppointmentUseCase.Command(appointmentId, clientId, LocalDateTime.now().plusDays(3));

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when service not found")
    void shouldThrowWhenServiceNotFound() {
        RescheduleAppointmentUseCase.Command command =
                new RescheduleAppointmentUseCase.Command(appointmentId, clientId, LocalDateTime.now().plusDays(3));

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(beautyServiceRepository.findById(serviceId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
