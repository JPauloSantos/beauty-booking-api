package com.beautyscheduler.adapter.out.persistence.adapter;

import com.beautyscheduler.adapter.out.persistence.mapper.ProfessionalMapper;
import com.beautyscheduler.adapter.out.persistence.repository.ProfessionalJpaRepository;
import com.beautyscheduler.application.port.out.ProfessionalRepositoryPort;
import com.beautyscheduler.domain.entity.Professional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProfessionalPersistenceAdapter implements ProfessionalRepositoryPort {

    private final ProfessionalJpaRepository repository;

    public ProfessionalPersistenceAdapter(ProfessionalJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Professional save(Professional professional) {
        return ProfessionalMapper.toDomain(repository.save(ProfessionalMapper.toJpa(professional)));
    }

    @Override
    public Optional<Professional> findById(UUID id) {
        return repository.findById(id).map(ProfessionalMapper::toDomain);
    }

    @Override
    public List<Professional> findByEstablishmentId(UUID establishmentId) {
        return repository.findByEstablishmentId(establishmentId).stream()
                .map(ProfessionalMapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
