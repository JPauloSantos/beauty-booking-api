package com.beautyscheduler.application.usecase.establishment;

import com.beautyscheduler.application.port.in.establishment.SearchEstablishmentUseCase;
import com.beautyscheduler.application.port.out.EstablishmentRepositoryPort;
import com.beautyscheduler.domain.entity.Establishment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SearchEstablishmentService implements SearchEstablishmentUseCase {

    private final EstablishmentRepositoryPort establishmentRepository;

    public SearchEstablishmentService(EstablishmentRepositoryPort establishmentRepository) {
        this.establishmentRepository = establishmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Establishment> search(Filters filters) {
        return establishmentRepository.search(
                filters.name(),
                filters.city(),
                filters.serviceName(),
                filters.minRating(),
                filters.minPrice(),
                filters.maxPrice()
        );
    }
}
