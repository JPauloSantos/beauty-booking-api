package com.beautyscheduler.domain.entity;

import com.beautyscheduler.domain.exception.AppointmentConflictException;
import com.beautyscheduler.domain.exception.DomainException;

import java.time.LocalDateTime;
import java.util.UUID;

public class Appointment {

    private UUID id;
    private UUID clientId;
    private UUID professionalId;
    private UUID serviceId;
    private UUID establishmentId;
    private LocalDateTime scheduledAt;
    private LocalDateTime endAt;
    private AppointmentStatus status;
    private String clientNotes;
    private LocalDateTime createdAt;

    public enum AppointmentStatus {
        PENDING, CONFIRMED, COMPLETED, CANCELLED, NO_SHOW
    }

    public Appointment() {}

    public Appointment(UUID id, UUID clientId, UUID professionalId, UUID serviceId,
                        UUID establishmentId, LocalDateTime scheduledAt, LocalDateTime endAt,
                        AppointmentStatus status, String clientNotes, LocalDateTime createdAt) {
        this.id = id;
        this.clientId = clientId;
        this.professionalId = professionalId;
        this.serviceId = serviceId;
        this.establishmentId = establishmentId;
        this.scheduledAt = scheduledAt;
        this.endAt = endAt;
        this.status = status;
        this.clientNotes = clientNotes;
        this.createdAt = createdAt;
    }

    public static Appointment create(UUID clientId, UUID professionalId, UUID serviceId,
                                      UUID establishmentId, LocalDateTime scheduledAt,
                                      int durationMinutes, String clientNotes) {
        if (scheduledAt.isBefore(LocalDateTime.now())) {
            throw new DomainException("Cannot schedule an appointment in the past.");
        }
        return new Appointment(
                UUID.randomUUID(),
                clientId,
                professionalId,
                serviceId,
                establishmentId,
                scheduledAt,
                scheduledAt.plusMinutes(durationMinutes),
                AppointmentStatus.PENDING,
                clientNotes,
                LocalDateTime.now()
        );
    }

    public void confirm() {
        if (this.status != AppointmentStatus.PENDING) {
            throw new DomainException("Only PENDING appointments can be confirmed.");
        }
        this.status = AppointmentStatus.CONFIRMED;
    }

    public void cancel() {
        if (this.status == AppointmentStatus.COMPLETED || this.status == AppointmentStatus.CANCELLED) {
            throw new DomainException("Cannot cancel a " + this.status + " appointment.");
        }
        this.status = AppointmentStatus.CANCELLED;
    }

    public void complete() {
        if (this.status != AppointmentStatus.CONFIRMED) {
            throw new DomainException("Only CONFIRMED appointments can be completed.");
        }
        this.status = AppointmentStatus.COMPLETED;
    }

    public void markNoShow() {
        if (this.status != AppointmentStatus.CONFIRMED) {
            throw new DomainException("Only CONFIRMED appointments can be marked as no-show.");
        }
        this.status = AppointmentStatus.NO_SHOW;
    }

    public void reschedule(LocalDateTime newScheduledAt, int durationMinutes) {
        if (this.status == AppointmentStatus.COMPLETED || this.status == AppointmentStatus.CANCELLED) {
            throw new DomainException("Cannot reschedule a " + this.status + " appointment.");
        }
        if (newScheduledAt.isBefore(LocalDateTime.now())) {
            throw new DomainException("Cannot reschedule to a past date.");
        }
        this.scheduledAt = newScheduledAt;
        this.endAt = newScheduledAt.plusMinutes(durationMinutes);
        this.status = AppointmentStatus.PENDING;
    }

    public boolean conflictsWith(Appointment other) {
        if (!this.professionalId.equals(other.professionalId)) return false;
        if (other.status == AppointmentStatus.CANCELLED || other.status == AppointmentStatus.NO_SHOW) return false;
        return this.scheduledAt.isBefore(other.endAt) && this.endAt.isAfter(other.scheduledAt);
    }

    public boolean isAtSameTime(LocalDateTime dateTime) {
        return !dateTime.isBefore(this.scheduledAt) && dateTime.isBefore(this.endAt);
    }

    public UUID getId() { return id; }
    public UUID getClientId() { return clientId; }
    public UUID getProfessionalId() { return professionalId; }
    public UUID getServiceId() { return serviceId; }
    public UUID getEstablishmentId() { return establishmentId; }
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public LocalDateTime getEndAt() { return endAt; }
    public AppointmentStatus getStatus() { return status; }
    public String getClientNotes() { return clientNotes; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(UUID id) { this.id = id; }
    public void setClientId(UUID clientId) { this.clientId = clientId; }
    public void setProfessionalId(UUID professionalId) { this.professionalId = professionalId; }
    public void setServiceId(UUID serviceId) { this.serviceId = serviceId; }
    public void setEstablishmentId(UUID establishmentId) { this.establishmentId = establishmentId; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }
    public void setEndAt(LocalDateTime endAt) { this.endAt = endAt; }
    public void setStatus(AppointmentStatus status) { this.status = status; }
    public void setClientNotes(String clientNotes) { this.clientNotes = clientNotes; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
