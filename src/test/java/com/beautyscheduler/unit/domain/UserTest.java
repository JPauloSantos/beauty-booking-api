package com.beautyscheduler.unit.domain;

import com.beautyscheduler.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("User Domain Tests")
class UserTest {

    @Test
    @DisplayName("Should create active user with correct fields")
    void shouldCreateActiveUser() {
        User user = User.create("Bob", "bob@email.com", "hash", User.UserRole.CLIENT, "11999999999");

        assertThat(user.getName()).isEqualTo("Bob");
        assertThat(user.getEmail()).isEqualTo("bob@email.com");
        assertThat(user.getRole()).isEqualTo(User.UserRole.CLIENT);
        assertThat(user.isActive()).isTrue();
        assertThat(user.getId()).isNotNull();
        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should deactivate user")
    void shouldDeactivateUser() {
        User user = User.create("Bob", "bob@email.com", "hash", User.UserRole.CLIENT, "11999999999");
        user.deactivate();

        assertThat(user.isActive()).isFalse();
    }

    @Test
    @DisplayName("Should create user with ESTABLISHMENT_OWNER role")
    void shouldCreateOwner() {
        User owner = User.create("Owner", "owner@email.com", "hash",
                User.UserRole.ESTABLISHMENT_OWNER, "11999999999");

        assertThat(owner.getRole()).isEqualTo(User.UserRole.ESTABLISHMENT_OWNER);
    }
}
