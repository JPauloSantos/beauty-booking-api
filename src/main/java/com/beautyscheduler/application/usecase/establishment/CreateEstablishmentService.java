package com.beautyscheduler.application.usecase.establishment;

import com.beautyscheduler.application.port.in.establishment.CreateEstablishmentUseCase;
import com.beautyscheduler.application.port.out.EstablishmentRepositoryPort;
import com.beautyscheduler.domain.entity.Establishment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateEstablishmentService implements CreateEstablishmentUseCase {

    private final EstablishmentRepositoryPort establishmentRepository;

    public CreateEstablishmentService(EstablishmentRepositoryPort establishmentRepository) {
        this.establishmentRepository = establishmentRepository;
    }

    @Override
    @Transactional
    public Establishment execute(Command command) {
        Establishment establishment = Establishment.create(
                command.name(),
                command.description(),
                command.address(),
                command.ownerId()
        );
        return establishmentRepository.save(establishment);
    }
}
