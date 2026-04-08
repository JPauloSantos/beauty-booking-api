package com.beautyscheduler.application.usecase.professional;

import com.beautyscheduler.application.port.in.professional.CreateProfessionalUseCase;
import com.beautyscheduler.application.port.out.EstablishmentRepositoryPort;
import com.beautyscheduler.application.port.out.ProfessionalRepositoryPort;
import com.beautyscheduler.domain.entity.Professional;
import com.beautyscheduler.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateProfessionalService implements CreateProfessionalUseCase {

    private final ProfessionalRepositoryPort professionalRepository;
    private final EstablishmentRepositoryPort establishmentRepository;

    public CreateProfessionalService(ProfessionalRepositoryPort professionalRepository,
                                      EstablishmentRepositoryPort establishmentRepository) {
        this.professionalRepository = professionalRepository;
        this.establishmentRepository = establishmentRepository;
    }

    @Override
    @Transactional
    public Professional execute(Command command) {
        establishmentRepository.findById(command.establishmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Establishment", command.establishmentId()));

        Professional professional = Professional.create(
                command.name(),
                command.bio(),
                command.specialties(),
                command.establishmentId(),
                command.userId(),
                command.hourlyRate()
        );
        return professionalRepository.save(professional);
    }
}
