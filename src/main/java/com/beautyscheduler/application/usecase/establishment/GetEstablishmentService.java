package com.beautyscheduler.application.usecase.establishment;

import com.beautyscheduler.application.port.in.establishment.GetEstablishmentUseCase;
import com.beautyscheduler.application.port.out.EstablishmentRepositoryPort;
import com.beautyscheduler.domain.entity.Establishment;
import com.beautyscheduler.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GetEstablishmentService implements GetEstablishmentUseCase {

    private final EstablishmentRepositoryPort establishmentRepository;

    public GetEstablishmentService(EstablishmentRepositoryPort establishmentRepository) {
        this.establishmentRepository = establishmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Establishment findById(UUID id) {
        return establishmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Establishment", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Establishment> findByOwner(UUID ownerId) {
        return establishmentRepository.findByOwnerId(ownerId);
    }
}
