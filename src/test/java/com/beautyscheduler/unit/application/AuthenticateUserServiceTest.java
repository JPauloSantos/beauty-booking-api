package com.beautyscheduler.unit.application;

import com.beautyscheduler.application.port.in.auth.AuthenticateUserUseCase;
import com.beautyscheduler.application.port.out.UserRepositoryPort;
import com.beautyscheduler.application.usecase.auth.AuthenticateUserService;
import com.beautyscheduler.domain.entity.User;
import com.beautyscheduler.domain.exception.UnauthorizedException;
import com.beautyscheduler.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticateUserService Unit Tests")
class AuthenticateUserServiceTest {

    @Mock private UserRepositoryPort userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;

    private AuthenticateUserService service;

    private User activeUser;

    @BeforeEach
    void setUp() {
        service = new AuthenticateUserService(userRepository, passwordEncoder, jwtTokenProvider);
        activeUser = User.create("Alice", "alice@email.com", "hashedPass", User.UserRole.CLIENT, "11999999999");
    }

    @Test
    @DisplayName("Should return token on valid credentials")
    void shouldReturnTokenOnValidCredentials() {
        AuthenticateUserUseCase.Command command =
                new AuthenticateUserUseCase.Command("alice@email.com", "rawPass");

        when(userRepository.findByEmail("alice@email.com")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("rawPass", "hashedPass")).thenReturn(true);
        when(jwtTokenProvider.generateToken(anyString(), anyString())).thenReturn("jwt-token");

        AuthenticateUserUseCase.Result result = service.execute(command);

        assertThat(result.token()).isEqualTo("jwt-token");
        assertThat(result.email()).isEqualTo("alice@email.com");
        assertThat(result.role()).isEqualTo("CLIENT");
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when user not found")
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail("unknown@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(
                new AuthenticateUserUseCase.Command("unknown@email.com", "pass")))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when password is wrong")
    void shouldThrowWhenPasswordWrong() {
        when(userRepository.findByEmail("alice@email.com")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("wrongPass", "hashedPass")).thenReturn(false);

        assertThatThrownBy(() -> service.execute(
                new AuthenticateUserUseCase.Command("alice@email.com", "wrongPass")))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when account is deactivated")
    void shouldThrowWhenAccountDeactivated() {
        activeUser.deactivate();
        when(userRepository.findByEmail("alice@email.com")).thenReturn(Optional.of(activeUser));

        assertThatThrownBy(() -> service.execute(
                new AuthenticateUserUseCase.Command("alice@email.com", "anyPass")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("deactivated");
    }
}
