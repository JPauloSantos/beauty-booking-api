package com.beautyscheduler.application.port.in.establishment;

import com.beautyscheduler.domain.entity.Establishment;

import java.util.List;
import java.util.UUID;

public interface GetEstablishmentUseCase {

    Establishment findById(UUID id);

    List<Establishment> findByOwner(UUID ownerId);
}
