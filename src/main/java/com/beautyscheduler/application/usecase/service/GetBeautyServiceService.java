package com.beautyscheduler.application.usecase.service;

import com.beautyscheduler.application.port.in.service.GetBeautyServiceUseCase;
import com.beautyscheduler.application.port.out.BeautyServiceRepositoryPort;
import com.beautyscheduler.domain.entity.BeautyService;
import com.beautyscheduler.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GetBeautyServiceService implements GetBeautyServiceUseCase {

    private final BeautyServiceRepositoryPort serviceRepository;

    public GetBeautyServiceService(BeautyServiceRepositoryPort serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public BeautyService findById(UUID id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BeautyService", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BeautyService> findByEstablishment(UUID establishmentId) {
        return serviceRepository.findByEstablishmentId(establishmentId);
    }
}
