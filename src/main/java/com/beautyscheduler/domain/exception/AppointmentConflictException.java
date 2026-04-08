package com.beautyscheduler.domain.exception;

public class AppointmentConflictException extends DomainException {
    public AppointmentConflictException(String message) {
        super(message);
    }
}
