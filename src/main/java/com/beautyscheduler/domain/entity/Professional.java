package com.beautyscheduler.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Professional {

    private UUID id;
    private String name;
    private String bio;
    private List<String> specialties;
    private UUID establishmentId;
    private UUID userId;
    private BigDecimal hourlyRate;
    private boolean active;
    private LocalDateTime createdAt;

    public Professional() {
        this.specialties = new ArrayList<>();
    }

    public Professional(UUID id, String name, String bio, List<String> specialties,
                         UUID establishmentId, UUID userId, BigDecimal hourlyRate,
                         boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.specialties = specialties != null ? specialties : new ArrayList<>();
        this.establishmentId = establishmentId;
        this.userId = userId;
        this.hourlyRate = hourlyRate;
        this.active = active;
        this.createdAt = createdAt;
    }

    public static Professional create(String name, String bio, List<String> specialties,
                                       UUID establishmentId, UUID userId, BigDecimal hourlyRate) {
        return new Professional(
                UUID.randomUUID(),
                name,
                bio,
                specialties,
                establishmentId,
                userId,
                hourlyRate,
                true,
                LocalDateTime.now()
        );
    }

    public boolean isAvailableAt(LocalDateTime dateTime, List<Appointment> existingAppointments) {
        return existingAppointments.stream()
                .filter(a -> a.getProfessionalId().equals(this.id))
                .filter(a -> a.getStatus() == Appointment.AppointmentStatus.CONFIRMED
                          || a.getStatus() == Appointment.AppointmentStatus.PENDING)
                .noneMatch(a -> a.isAtSameTime(dateTime));
    }

    public void deactivate() {
        this.active = false;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getBio() { return bio; }
    public List<String> getSpecialties() { return specialties; }
    public UUID getEstablishmentId() { return establishmentId; }
    public UUID getUserId() { return userId; }
    public BigDecimal getHourlyRate() { return hourlyRate; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(UUID id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setBio(String bio) { this.bio = bio; }
    public void setSpecialties(List<String> specialties) { this.specialties = specialties; }
    public void setEstablishmentId(UUID establishmentId) { this.establishmentId = establishmentId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }
    public void setActive(boolean active) { this.active = active; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
