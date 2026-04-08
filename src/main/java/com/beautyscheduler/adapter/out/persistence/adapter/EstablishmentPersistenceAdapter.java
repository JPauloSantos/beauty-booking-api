package com.beautyscheduler.adapter.out.persistence.adapter;

import com.beautyscheduler.adapter.out.persistence.mapper.EstablishmentMapper;
import com.beautyscheduler.adapter.out.persistence.repository.EstablishmentJpaRepository;
import com.beautyscheduler.application.port.out.EstablishmentRepositoryPort;
import com.beautyscheduler.domain.entity.Establishment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class EstablishmentPersistenceAdapter implements EstablishmentRepositoryPort {

    private final EstablishmentJpaRepository repository;

    public EstablishmentPersistenceAdapter(EstablishmentJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Establishment save(Establishment establishment) {
        return EstablishmentMapper.toDomain(repository.save(EstablishmentMapper.toJpa(establishment)));
    }

    @Override
    public Optional<Establishment> findById(UUID id) {
        return repository.findById(id).map(EstablishmentMapper::toDomain);
    }

    @Override
    public List<Establishment> findByOwnerId(UUID ownerId) {
        return repository.findByOwnerId(ownerId).stream()
                .map(EstablishmentMapper::toDomain).toList();
    }

    @Override
    public List<Establishment> search(String name, String city, String serviceName,
                                       Double minRating, BigDecimal minPrice, BigDecimal maxPrice) {
        return repository.search(name, city, serviceName, minPrice, maxPrice).stream()
                .map(EstablishmentMapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
