package com.beautyscheduler.adapter.out.persistence.adapter;

import com.beautyscheduler.adapter.out.persistence.mapper.BeautyServiceMapper;
import com.beautyscheduler.adapter.out.persistence.repository.BeautyServiceJpaRepository;
import com.beautyscheduler.application.port.out.BeautyServiceRepositoryPort;
import com.beautyscheduler.domain.entity.BeautyService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class BeautyServicePersistenceAdapter implements BeautyServiceRepositoryPort {

    private final BeautyServiceJpaRepository repository;

    public BeautyServicePersistenceAdapter(BeautyServiceJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public BeautyService save(BeautyService service) {
        return BeautyServiceMapper.toDomain(repository.save(BeautyServiceMapper.toJpa(service)));
    }

    @Override
    public Optional<BeautyService> findById(UUID id) {
        return repository.findById(id).map(BeautyServiceMapper::toDomain);
    }

    @Override
    public List<BeautyService> findByEstablishmentId(UUID establishmentId) {
        return repository.findByEstablishmentId(establishmentId).stream()
                .map(BeautyServiceMapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
