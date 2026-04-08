package com.beautyscheduler.application.usecase.auth;

import com.beautyscheduler.application.port.in.auth.RegisterUserUseCase;
import com.beautyscheduler.application.port.out.UserRepositoryPort;
import com.beautyscheduler.domain.entity.User;
import com.beautyscheduler.domain.exception.DomainException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserService(UserRepositoryPort userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User execute(Command command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new DomainException("Email already registered: " + command.email());
        }
        User user = User.create(
                command.name(),
                command.email(),
                passwordEncoder.encode(command.password()),
                command.role(),
                command.phone()
        );
        return userRepository.save(user);
    }
}
