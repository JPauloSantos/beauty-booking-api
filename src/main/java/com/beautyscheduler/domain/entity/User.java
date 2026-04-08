package com.beautyscheduler.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {

    private UUID id;
    private String name;
    private String email;
    private String passwordHash;
    private UserRole role;
    private String phone;
    private LocalDateTime createdAt;
    private boolean active;

    public enum UserRole {
        CLIENT, PROFESSIONAL, ESTABLISHMENT_OWNER, ADMIN
    }

    public User() {}

    public User(UUID id, String name, String email, String passwordHash,
                UserRole role, String phone, LocalDateTime createdAt, boolean active) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.phone = phone;
        this.createdAt = createdAt;
        this.active = active;
    }

    public static User create(String name, String email, String passwordHash,
                               UserRole role, String phone) {
        return new User(
                UUID.randomUUID(),
                name,
                email,
                passwordHash,
                role,
                phone,
                LocalDateTime.now(),
                true
        );
    }

    public void deactivate() {
        this.active = false;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public UserRole getRole() { return role; }
    public String getPhone() { return phone; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isActive() { return active; }

    public void setId(UUID id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRole(UserRole role) { this.role = role; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setActive(boolean active) { this.active = active; }
}
