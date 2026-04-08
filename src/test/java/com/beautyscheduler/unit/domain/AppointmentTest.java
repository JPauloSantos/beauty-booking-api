package com.beautyscheduler.unit.domain;

import com.beautyscheduler.domain.entity.Appointment;
import com.beautyscheduler.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Appointment Domain Tests")
class AppointmentTest {

    private static final UUID CLIENT_ID = UUID.randomUUID();
    private static final UUID PROFESSIONAL_ID = UUID.randomUUID();
    private static final UUID SERVICE_ID = UUID.randomUUID();
    private static final UUID ESTABLISHMENT_ID = UUID.randomUUID();

    @Nested
    @DisplayName("Appointment creation")
    class Creation {

        @Test
        @DisplayName("Should create appointment with PENDING status")
        void shouldCreateWithPendingStatus() {
            LocalDateTime future = LocalDateTime.now().plusDays(1);
            Appointment appointment = Appointment.create(
                    CLIENT_ID, PROFESSIONAL_ID, SERVICE_ID, ESTABLISHMENT_ID, future, 60, "notes");

            assertThat(appointment.getStatus()).isEqualTo(Appointment.AppointmentStatus.PENDING);
            assertThat(appointment.getId()).isNotNull();
            assertThat(appointment.getScheduledAt()).isEqualTo(future);
            assertThat(appointment.getEndAt()).isEqualTo(future.plusMinutes(60));
        }

        @Test
        @DisplayName("Should throw DomainException when scheduling in the past")
        void shouldThrowWhenSchedulingInPast() {
            LocalDateTime past = LocalDateTime.now().minusDays(1);
            assertThatThrownBy(() ->
                    Appointment.create(CLIENT_ID, PROFESSIONAL_ID, SERVICE_ID,
                            ESTABLISHMENT_ID, past, 60, null))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("past");
        }
    }

    @Nested
    @DisplayName("Appointment status transitions")
    class StatusTransitions {

        @Test
        @DisplayName("Should confirm a PENDING appointment")
        void shouldConfirmPending() {
            Appointment appointment = createFutureAppointment();
            appointment.confirm();
            assertThat(appointment.getStatus()).isEqualTo(Appointment.AppointmentStatus.CONFIRMED);
        }

        @Test
        @DisplayName("Should cancel a PENDING appointment")
        void shouldCancelPending() {
            Appointment appointment = createFutureAppointment();
            appointment.cancel();
            assertThat(appointment.getStatus()).isEqualTo(Appointment.AppointmentStatus.CANCELLED);
        }

        @Test
        @DisplayName("Should cancel a CONFIRMED appointment")
        void shouldCancelConfirmed() {
            Appointment appointment = createFutureAppointment();
            appointment.confirm();
            appointment.cancel();
            assertThat(appointment.getStatus()).isEqualTo(Appointment.AppointmentStatus.CANCELLED);
        }

        @Test
        @DisplayName("Should throw when confirming non-PENDING appointment")
        void shouldThrowWhenConfirmingNonPending() {
            Appointment appointment = createFutureAppointment();
            appointment.cancel();
            assertThatThrownBy(appointment::confirm).isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("Should mark confirmed appointment as no-show")
        void shouldMarkNoShow() {
            Appointment appointment = createFutureAppointment();
            appointment.confirm();
            appointment.markNoShow();
            assertThat(appointment.getStatus()).isEqualTo(Appointment.AppointmentStatus.NO_SHOW);
        }
    }

    @Nested
    @DisplayName("Appointment conflict detection")
    class ConflictDetection {

        @Test
        @DisplayName("Should detect conflict with overlapping appointments")
        void shouldDetectConflict() {
            LocalDateTime base = LocalDateTime.now().plusDays(1).withHour(10);
            Appointment a1 = Appointment.create(CLIENT_ID, PROFESSIONAL_ID, SERVICE_ID,
                    ESTABLISHMENT_ID, base, 60, null);
            a1.confirm();

            Appointment a2 = Appointment.create(UUID.randomUUID(), PROFESSIONAL_ID, SERVICE_ID,
                    ESTABLISHMENT_ID, base.plusMinutes(30), 60, null);

            assertThat(a2.conflictsWith(a1)).isTrue();
        }

        @Test
        @DisplayName("Should not detect conflict with non-overlapping appointments")
        void shouldNotDetectConflict() {
            LocalDateTime base = LocalDateTime.now().plusDays(1).withHour(10);
            Appointment a1 = Appointment.create(CLIENT_ID, PROFESSIONAL_ID, SERVICE_ID,
                    ESTABLISHMENT_ID, base, 60, null);
            a1.confirm();

            Appointment a2 = Appointment.create(UUID.randomUUID(), PROFESSIONAL_ID, SERVICE_ID,
                    ESTABLISHMENT_ID, base.plusMinutes(60), 60, null);

            assertThat(a2.conflictsWith(a1)).isFalse();
        }
    }

    private Appointment createFutureAppointment() {
        return Appointment.create(CLIENT_ID, PROFESSIONAL_ID, SERVICE_ID,
                ESTABLISHMENT_ID, LocalDateTime.now().plusDays(1), 60, "test");
    }
}
