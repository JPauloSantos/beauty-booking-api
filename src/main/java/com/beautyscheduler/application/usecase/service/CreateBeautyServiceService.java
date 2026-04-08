package com.beautyscheduler.application.usecase.service;

import com.beautyscheduler.application.port.in.service.CreateBeautyServiceUseCase;
import com.beautyscheduler.application.port.out.BeautyServiceRepositoryPort;
import com.beautyscheduler.application.port.out.EstablishmentRepositoryPort;
import com.beautyscheduler.domain.entity.BeautyService;
import com.beautyscheduler.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateBeautyServiceService implements CreateBeautyServiceUseCase {

    private final BeautyServiceRepositoryPort serviceRepository;
    private final EstablishmentRepositoryPort establishmentRepository;

    public CreateBeautyServiceService(BeautyServiceRepositoryPort serviceRepository,
                                       EstablishmentRepositoryPort establishmentRepository) {
        this.serviceRepository = serviceRepository;
        this.establishmentRepository = establishmentRepository;
    }

    @Override
    @Transactional
    public BeautyService execute(Command command) {
        establishmentRepository.findById(command.establishmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Establishment", command.establishmentId()));

        BeautyService service = BeautyService.create(
                command.name(),
                command.description(),
                command.durationMinutes(),
                command.price(),
                command.establishmentId()
        );
        return serviceRepository.save(service);
    }
}
