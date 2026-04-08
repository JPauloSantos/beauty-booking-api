package com.beautyscheduler.unit.application;

import com.beautyscheduler.application.port.in.appointment.CreateAppointmentUseCase;
import com.beautyscheduler.application.port.out.*;
import com.beautyscheduler.application.usecase.appointment.CreateAppointmentService;
import com.beautyscheduler.domain.entity.*;
import com.beautyscheduler.domain.exception.AppointmentConflictException;
import com.beautyscheduler.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateAppointmentService Unit Tests")
class CreateAppointmentServiceTest {

    @Mock private AppointmentRepositoryPort appointmentRepository;
    @Mock private BeautyServiceRepositoryPort beautyServiceRepository;
    @Mock private UserRepositoryPort userRepository;
    @Mock private ProfessionalRepositoryPort professionalRepository;
    @Mock private NotificationPort notificationPort;

    private CreateAppointmentService service;

    private UUID clientId;
    private UUID professionalId;
    private UUID serviceId;
    private UUID establishmentId;
    private BeautyService beautyService;
    private User client;
    private Professional professional;

    @BeforeEach
    void setUp() {
        service = new CreateAppointmentService(appointmentRepository, beautyServiceRepository,
                userRepository, professionalRepository, notificationPort);

        clientId = UUID.randomUUID();
        professionalId = UUID.randomUUID();
        serviceId = UUID.randomUUID();
        establishmentId = UUID.randomUUID();

        beautyService = new BeautyService(serviceId, "Haircut", "Basic haircut",
                60, BigDecimal.valueOf(50), establishmentId, true, LocalDateTime.now());

        client = User.create("John", "john@email.com", "hash",
                User.UserRole.CLIENT, "11999999999");
        client.setId(clientId);

        professional = Professional.create("Jane", "Pro stylist",
                new ArrayList<>(), establishmentId, UUID.randomUUID(), BigDecimal.valueOf(80));
        professional.setId(professionalId);
    }

    @Test
    @DisplayName("Should create appointment successfully")
    void shouldCreateAppointmentSuccessfully() {
        LocalDateTime scheduledAt = LocalDateTime.now().plusDays(1);
        CreateAppointmentUseCase.Command command = new CreateAppointmentUseCase.Command(
                clientId, professionalId, serviceId, establishmentId, scheduledAt, "Please be on time");

        when(beautyServiceRepository.findById(serviceId)).thenReturn(Optional.of(beautyService));
        when(professionalRepository.findById(professionalId)).thenReturn(Optional.of(professional));
        when(userRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(userRepository.findById(professionalId)).thenReturn(Optional.of(client));
        when(appointmentRepository.findByProfessionalIdAndDateRange(any(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(appointmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Appointment result = service.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getClientId()).isEqualTo(clientId);
        assertThat(result.getStatus()).isEqualTo(Appointment.AppointmentStatus.PENDING);
        verify(notificationPort).sendAppointmentConfirmation(any(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when service not found")
    void shouldThrowWhenServiceNotFound() {
        when(beautyServiceRepository.findById(serviceId)).thenReturn(Optional.empty());

        CreateAppointmentUseCase.Command command = new CreateAppointmentUseCase.Command(
                clientId, professionalId, serviceId, establishmentId,
                LocalDateTime.now().plusDays(1), null);

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should throw AppointmentConflictException when professional is busy")
    void shouldThrowWhenProfessionalIsBusy() {
        LocalDateTime scheduledAt = LocalDateTime.now().plusDays(1).withHour(10);
        Appointment existing = Appointment.create(UUID.randomUUID(), professionalId,
                serviceId, establishmentId, scheduledAt.minusMinutes(30), 90, null);
        existing.confirm();

        when(beautyServiceRepository.findById(serviceId)).thenReturn(Optional.of(beautyService));
        when(professionalRepository.findById(professionalId)).thenReturn(Optional.of(professional));
        when(userRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(appointmentRepository.findByProfessionalIdAndDateRange(any(), any(), any()))
                .thenReturn(java.util.List.of(existing));

        CreateAppointmentUseCase.Command command = new CreateAppointmentUseCase.Command(
                clientId, professionalId, serviceId, establishmentId, scheduledAt, null);

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(AppointmentConflictException.class);
    }
}
