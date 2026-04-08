package com.beautyscheduler.application.port.in.auth;

import com.beautyscheduler.domain.entity.User;

public interface RegisterUserUseCase {

    record Command(String name, String email, String password,
                   User.UserRole role, String phone) {}

    User execute(Command command);
}
