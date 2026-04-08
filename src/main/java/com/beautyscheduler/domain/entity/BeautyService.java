package com.beautyscheduler.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class BeautyService {

    private UUID id;
    private String name;
    private String description;
    private int durationMinutes;
    private BigDecimal price;
    private UUID establishmentId;
    private boolean active;
    private LocalDateTime createdAt;

    public BeautyService() {}

    public BeautyService(UUID id, String name, String description, int durationMinutes,
                          BigDecimal price, UUID establishmentId, boolean active,
                          LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.price = price;
        this.establishmentId = establishmentId;
        this.active = active;
        this.createdAt = createdAt;
    }

    public static BeautyService create(String name, String description, int durationMinutes,
                                        BigDecimal price, UUID establishmentId) {
        return new BeautyService(
                UUID.randomUUID(),
                name,
                description,
                durationMinutes,
                price,
                establishmentId,
                true,
                LocalDateTime.now()
        );
    }

    public void deactivate() {
        this.active = false;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getDurationMinutes() { return durationMinutes; }
    public BigDecimal getPrice() { return price; }
    public UUID getEstablishmentId() { return establishmentId; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(UUID id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setEstablishmentId(UUID establishmentId) { this.establishmentId = establishmentId; }
    public void setActive(boolean active) { this.active = active; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
