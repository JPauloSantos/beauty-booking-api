package com.beautyscheduler.unit.application;

import com.beautyscheduler.application.port.out.AppointmentRepositoryPort;
import com.beautyscheduler.application.port.out.UserRepositoryPort;
import com.beautyscheduler.application.usecase.appointment.MarkNoShowService;
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
@DisplayName("MarkNoShowService Unit Tests")
class MarkNoShowServiceTest {

    @Mock private AppointmentRepositoryPort appointmentRepository;
    @Mock private UserRepositoryPort userRepository;

    private MarkNoShowService service;

    private UUID appointmentId;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        service = new MarkNoShowService(appointmentRepository, userRepository);

        appointmentId = UUID.randomUUID();
        appointment = new Appointment(
                appointmentId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2),
                Appointment.AppointmentStatus.CONFIRMED, null, LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Should mark no-show when requester is establishment owner")
    void shouldMarkNoShowForOwner() {
        User owner = User.create("Owner", "o@email.com", "hash", User.UserRole.ESTABLISHMENT_OWNER, "11999999999");
        UUID ownerId = owner.getId();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(appointmentRepository.save(any())).thenReturn(appointment);

        service.execute(appointmentId, ownerId);

        assertThat(appointment.getStatus()).isEqualTo(Appointment.AppointmentStatus.NO_SHOW);
    }

    @Test
    @DisplayName("Should mark no-show when requester is professional")
    void shouldMarkNoShowForProfessional() {
        User professional = User.create("Pro", "p@email.com", "hash", User.UserRole.PROFESSIONAL, "11999999999");
        UUID professionalId = professional.getId();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(userRepository.findById(professionalId)).thenReturn(Optional.of(professional));
        when(appointmentRepository.save(any())).thenReturn(appointment);

        service.execute(appointmentId, professionalId);

        assertThat(appointment.getStatus()).isEqualTo(Appointment.AppointmentStatus.NO_SHOW);
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when requester is a client")
    void shouldThrowWhenRequesterIsClient() {
        User client = User.create("Client", "c@email.com", "hash", User.UserRole.CLIENT, "11999999999");
        UUID clientId = client.getId();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(userRepository.findById(clientId)).thenReturn(Optional.of(client));

        assertThatThrownBy(() -> service.execute(appointmentId, clientId))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when appointment not found")
    void shouldThrowWhenAppointmentNotFound() {
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(appointmentId, UUID.randomUUID()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
