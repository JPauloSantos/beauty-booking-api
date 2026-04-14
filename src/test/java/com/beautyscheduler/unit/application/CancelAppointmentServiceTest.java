package com.beautyscheduler.unit.application;

import com.beautyscheduler.application.port.out.AppointmentRepositoryPort;
import com.beautyscheduler.application.port.out.NotificationPort;
import com.beautyscheduler.application.port.out.UserRepositoryPort;
import com.beautyscheduler.application.usecase.appointment.CancelAppointmentService;
import com.beautyscheduler.domain.entity.Appointment;
import com.beautyscheduler.domain.entity.User;
import com.beautyscheduler.domain.exception.ResourceNotFoundException;
import com.beautyscheduler.domain.exception.UnauthorizedException;
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
@DisplayName("CancelAppointmentService Unit Tests")
class CancelAppointmentServiceTest {

    @Mock private AppointmentRepositoryPort appointmentRepository;
    @Mock private UserRepositoryPort userRepository;
    @Mock private NotificationPort notificationPort;

    private CancelAppointmentService service;

    private UUID clientId;
    private UUID appointmentId;
    private Appointment appointment;
    private User clientUser;
    private User ownerUser;

    @BeforeEach
    void setUp() {
        service = new CancelAppointmentService(appointmentRepository, userRepository, notificationPort);

        clientId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();

        appointment = new Appointment(
                appointmentId, clientId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1),
                Appointment.AppointmentStatus.CONFIRMED, null, LocalDateTime.now()
        );

        clientUser = User.create("Client", "client@email.com", "hash", User.UserRole.CLIENT, "11999999999");
        clientUser.setId(clientId);

        ownerUser = User.create("Owner", "owner@email.com", "hash", User.UserRole.ESTABLISHMENT_OWNER, "11999999999");
        ownerUser.setId(UUID.randomUUID());
    }

    @Test
    @DisplayName("Should cancel appointment when requester is the client")
    void shouldCancelWhenRequesterIsClient() {
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(userRepository.findById(clientId)).thenReturn(Optional.of(clientUser));
        when(userRepository.findById(appointment.getClientId())).thenReturn(Optional.of(clientUser));
        when(userRepository.findById(appointment.getProfessionalId())).thenReturn(Optional.empty());
        when(appointmentRepository.save(any())).thenReturn(appointment);

        service.execute(appointmentId, clientId);

        assertThat(appointment.getStatus()).isEqualTo(Appointment.AppointmentStatus.CANCELLED);
        verify(notificationPort).sendAppointmentCancellation(any(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should cancel appointment when requester is establishment owner")
    void shouldCancelWhenRequesterIsOwner() {
        UUID ownerId = ownerUser.getId();
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(ownerUser));
        when(userRepository.findById(appointment.getClientId())).thenReturn(Optional.of(clientUser));
        when(userRepository.findById(appointment.getProfessionalId())).thenReturn(Optional.empty());
        when(appointmentRepository.save(any())).thenReturn(appointment);

        service.execute(appointmentId, ownerId);

        assertThat(appointment.getStatus()).isEqualTo(Appointment.AppointmentStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when requester is not client nor owner")
    void shouldThrowWhenRequesterIsUnauthorized() {
        User stranger = User.create("Stranger", "s@email.com", "hash", User.UserRole.PROFESSIONAL, "11999999999");
        UUID strangerId = stranger.getId();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(userRepository.findById(strangerId)).thenReturn(Optional.of(stranger));

        assertThatThrownBy(() -> service.execute(appointmentId, strangerId))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when appointment not found")
    void shouldThrowWhenAppointmentNotFound() {
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(appointmentId, clientId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when requester not found")
    void shouldThrowWhenRequesterNotFound() {
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(userRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(appointmentId, clientId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
