package com.beautyscheduler.domain.entity;

import com.beautyscheduler.domain.valueobject.Address;
import com.beautyscheduler.domain.valueobject.BusinessHours;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Establishment {

    private UUID id;
    private String name;
    private String description;
    private Address address;
    private List<BusinessHours> operatingHours;
    private List<String> photoUrls;
    private UUID ownerId;
    private boolean active;
    private LocalDateTime createdAt;

    public Establishment() {
        this.operatingHours = new ArrayList<>();
        this.photoUrls = new ArrayList<>();
    }

    public Establishment(UUID id, String name, String description, Address address,
                          List<BusinessHours> operatingHours, List<String> photoUrls,
                          UUID ownerId, boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.operatingHours = operatingHours != null ? operatingHours : new ArrayList<>();
        this.photoUrls = photoUrls != null ? photoUrls : new ArrayList<>();
        this.ownerId = ownerId;
        this.active = active;
        this.createdAt = createdAt;
    }

    public static Establishment create(String name, String description, Address address,
                                        UUID ownerId) {
        return new Establishment(
                UUID.randomUUID(),
                name,
                description,
                address,
                new ArrayList<>(),
                new ArrayList<>(),
                ownerId,
                true,
                LocalDateTime.now()
        );
    }

    public boolean isOpenAt(LocalDateTime dateTime) {
        DayOfWeek day = dateTime.getDayOfWeek();
        LocalTime time = dateTime.toLocalTime();
        return operatingHours.stream()
                .filter(bh -> bh.getDayOfWeek() == day)
                .anyMatch(bh -> !time.isBefore(bh.getOpenTime()) && !time.isAfter(bh.getCloseTime()));
    }

    public void addPhoto(String photoUrl) {
        this.photoUrls.add(photoUrl);
    }

    public void addOperatingHours(BusinessHours hours) {
        this.operatingHours.add(hours);
    }

    public void deactivate() {
        this.active = false;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Address getAddress() { return address; }
    public List<BusinessHours> getOperatingHours() { return operatingHours; }
    public List<String> getPhotoUrls() { return photoUrls; }
    public UUID getOwnerId() { return ownerId; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(UUID id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setAddress(Address address) { this.address = address; }
    public void setOperatingHours(List<BusinessHours> operatingHours) { this.operatingHours = operatingHours; }
    public void setPhotoUrls(List<String> photoUrls) { this.photoUrls = photoUrls; }
    public void setOwnerId(UUID ownerId) { this.ownerId = ownerId; }
    public void setActive(boolean active) { this.active = active; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
