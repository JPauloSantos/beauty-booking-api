package com.beautyscheduler.adapter.in.web.controller;

import com.beautyscheduler.adapter.in.web.dto.request.LoginRequest;
import com.beautyscheduler.adapter.in.web.dto.request.RegisterUserRequest;
import com.beautyscheduler.adapter.in.web.dto.response.AuthResponse;
import com.beautyscheduler.application.port.in.auth.AuthenticateUserUseCase;
import com.beautyscheduler.application.port.in.auth.RegisterUserUseCase;
import com.beautyscheduler.domain.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User registration and login")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final AuthenticateUserUseCase authenticateUserUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase,
                           AuthenticateUserUseCase authenticateUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.authenticateUserUseCase = authenticateUserUseCase;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterUserRequest request) {
        registerUserUseCase.execute(new RegisterUserUseCase.Command(
                request.name(), request.email(), request.password(),
                request.role(), request.phone()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and get JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthenticateUserUseCase.Result result = authenticateUserUseCase.execute(
                new AuthenticateUserUseCase.Command(request.email(), request.password())
        );
        return ResponseEntity.ok(new AuthResponse(result.token(), result.email(), result.role()));
    }
}
