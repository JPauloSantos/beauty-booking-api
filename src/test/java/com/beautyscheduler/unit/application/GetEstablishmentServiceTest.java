package com.beautyscheduler.unit.application;

import com.beautyscheduler.application.port.out.EstablishmentRepositoryPort;
import com.beautyscheduler.application.usecase.establishment.GetEstablishmentService;
import com.beautyscheduler.domain.entity.Establishment;
import com.beautyscheduler.domain.exception.ResourceNotFoundException;
import com.beautyscheduler.domain.valueobject.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetEstablishmentService Unit Tests")
class GetEstablishmentServiceTest {

    @Mock private EstablishmentRepositoryPort establishmentRepository;

    private GetEstablishmentService service;

    private UUID ownerId;
    private Establishment establishment;

    @BeforeEach
    void setUp() {
        service = new GetEstablishmentService(establishmentRepository);
        ownerId = UUID.randomUUID();

        Address address = new Address("Rua A", "1", null, "Centro", "São Paulo", "SP", "01310-100", null, null);
        establishment = Establishment.create("Salão Teste", "Desc", address, ownerId);
    }

    @Test
    @DisplayName("Should return establishment when found by ID")
    void shouldFindById() {
        UUID id = establishment.getId();
        when(establishmentRepository.findById(id)).thenReturn(Optional.of(establishment));

        Establishment result = service.findById(id);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Salão Teste");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when establishment not found")
    void shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(establishmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should return list of establishments by owner")
    void shouldFindByOwner() {
        when(establishmentRepository.findByOwnerId(ownerId)).thenReturn(List.of(establishment));

        List<Establishment> results = service.findByOwner(ownerId);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Salão Teste");
    }

    @Test
    @DisplayName("Should return empty list when owner has no establishments")
    void shouldReturnEmptyListForOwnerWithNoEstablishments() {
        when(establishmentRepository.findByOwnerId(ownerId)).thenReturn(List.of());

        List<Establishment> results = service.findByOwner(ownerId);

        assertThat(results).isEmpty();
    }
}
