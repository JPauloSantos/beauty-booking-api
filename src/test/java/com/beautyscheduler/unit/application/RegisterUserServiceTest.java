package com.beautyscheduler.unit.application;

import com.beautyscheduler.application.port.in.auth.RegisterUserUseCase;
import com.beautyscheduler.application.port.out.UserRepositoryPort;
import com.beautyscheduler.application.usecase.auth.RegisterUserService;
import com.beautyscheduler.domain.entity.User;
import com.beautyscheduler.domain.exception.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterUserService Unit Tests")
class RegisterUserServiceTest {

    @Mock private UserRepositoryPort userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    private RegisterUserService service;

    @BeforeEach
    void setUp() {
        service = new RegisterUserService(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() {
        RegisterUserUseCase.Command command = new RegisterUserUseCase.Command(
                "Alice", "alice@email.com", "password123",
                User.UserRole.CLIENT, "11999999999");

        when(userRepository.existsByEmail("alice@email.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User result = service.execute(command);

        assertThat(result.getEmail()).isEqualTo("alice@email.com");
        assertThat(result.getPasswordHash()).isEqualTo("hashed");
        assertThat(result.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should throw DomainException when email already registered")
    void shouldThrowWhenEmailExists() {
        RegisterUserUseCase.Command command = new RegisterUserUseCase.Command(
                "Alice", "existing@email.com", "password123",
                User.UserRole.CLIENT, "11999999999");

        when(userRepository.existsByEmail("existing@email.com")).thenReturn(true);

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("already registered");
    }
}
