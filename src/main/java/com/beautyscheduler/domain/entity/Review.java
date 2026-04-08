package com.beautyscheduler.domain.entity;

import com.beautyscheduler.domain.exception.DomainException;

import java.time.LocalDateTime;
import java.util.UUID;

public class Review {

    private UUID id;
    private UUID clientId;
    private UUID establishmentId;
    private UUID professionalId;
    private UUID appointmentId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    public Review() {}

    public Review(UUID id, UUID clientId, UUID establishmentId, UUID professionalId,
                   UUID appointmentId, int rating, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.clientId = clientId;
        this.establishmentId = establishmentId;
        this.professionalId = professionalId;
        this.appointmentId = appointmentId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public static Review create(UUID clientId, UUID establishmentId, UUID professionalId,
                                 UUID appointmentId, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new DomainException("Rating must be between 1 and 5.");
        }
        return new Review(
                UUID.randomUUID(),
                clientId,
                establishmentId,
                professionalId,
                appointmentId,
                rating,
                comment,
                LocalDateTime.now()
        );
    }

    public UUID getId() { return id; }
    public UUID getClientId() { return clientId; }
    public UUID getEstablishmentId() { return establishmentId; }
    public UUID getProfessionalId() { return professionalId; }
    public UUID getAppointmentId() { return appointmentId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(UUID id) { this.id = id; }
    public void setClientId(UUID clientId) { this.clientId = clientId; }
    public void setEstablishmentId(UUID establishmentId) { this.establishmentId = establishmentId; }
    public void setProfessionalId(UUID professionalId) { this.professionalId = professionalId; }
    public void setAppointmentId(UUID appointmentId) { this.appointmentId = appointmentId; }
    public void setRating(int rating) { this.rating = rating; }
    public void setComment(String comment) { this.comment = comment; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
