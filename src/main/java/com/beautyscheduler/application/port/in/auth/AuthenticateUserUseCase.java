package com.beautyscheduler.application.port.in.auth;

public interface AuthenticateUserUseCase {

    record Command(String email, String password) {}

    record Result(String token, String email, String role) {}

    Result execute(Command command);
}
