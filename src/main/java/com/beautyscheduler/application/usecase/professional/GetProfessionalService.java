package com.beautyscheduler.application.usecase.professional;

import com.beautyscheduler.application.port.in.professional.GetProfessionalUseCase;
import com.beautyscheduler.application.port.out.ProfessionalRepositoryPort;
import com.beautyscheduler.domain.entity.Professional;
import com.beautyscheduler.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GetProfessionalService implements GetProfessionalUseCase {

    private final ProfessionalRepositoryPort professionalRepository;

    public GetProfessionalService(ProfessionalRepositoryPort professionalRepository) {
        this.professionalRepository = professionalRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Professional findById(UUID id) {
        return professionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professional", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Professional> findByEstablishment(UUID establishmentId) {
        return professionalRepository.findByEstablishmentId(establishmentId);
    }
}
