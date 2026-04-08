package com.beautyscheduler.application.usecase.auth;

import com.beautyscheduler.application.port.in.auth.AuthenticateUserUseCase;
import com.beautyscheduler.application.port.out.UserRepositoryPort;
import com.beautyscheduler.domain.entity.User;
import com.beautyscheduler.domain.exception.UnauthorizedException;
import com.beautyscheduler.infrastructure.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateUserService implements AuthenticateUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthenticateUserService(UserRepositoryPort userRepository,
                                    PasswordEncoder passwordEncoder,
                                    JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Result execute(Command command) {
        User user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials."));

        if (!user.isActive()) {
            throw new UnauthorizedException("Account is deactivated.");
        }

        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials.");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name());
        return new Result(token, user.getEmail(), user.getRole().name());
    }
}
