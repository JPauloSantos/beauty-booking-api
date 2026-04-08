package com.beautyscheduler.application.port.in.establishment;

import com.beautyscheduler.domain.entity.Establishment;
import com.beautyscheduler.domain.valueobject.Address;

import java.util.UUID;

public interface CreateEstablishmentUseCase {

    record Command(String name, String description, Address address, UUID ownerId) {}

    Establishment execute(Command command);
}
