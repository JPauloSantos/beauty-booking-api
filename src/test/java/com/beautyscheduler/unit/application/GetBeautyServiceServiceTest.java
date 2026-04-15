package com.beautyscheduler.unit.application;

import com.beautyscheduler.application.port.out.BeautyServiceRepositoryPort;
import com.beautyscheduler.application.usecase.service.GetBeautyServiceService;
import com.beautyscheduler.domain.entity.BeautyService;
import com.beautyscheduler.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetBeautyServiceService Unit Tests")
class GetBeautyServiceServiceTest {

    @Mock private BeautyServiceRepositoryPort serviceRepository;

    private GetBeautyServiceService service;

    private UUID establishmentId;
    private BeautyService beautyService;

    @BeforeEach
    void setUp() {
        service = new GetBeautyServiceService(serviceRepository);
        establishmentId = UUID.randomUUID();
        beautyService = BeautyService.create("Corte Feminino", "Corte com lavagem",
                60, BigDecimal.valueOf(80), establishmentId);
    }

    @Test
    @DisplayName("Should return service when found by ID")
    void shouldFindById() {
        UUID id = beautyService.getId();
        when(serviceRepository.findById(id)).thenReturn(Optional.of(beautyService));

        BeautyService result = service.findById(id);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Corte Feminino");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when service not found")
    void shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(serviceRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should return services by establishment")
    void shouldFindByEstablishment() {
        when(serviceRepository.findByEstablishmentId(establishmentId)).thenReturn(List.of(beautyService));

        List<BeautyService> result = service.findByEstablishment(establishmentId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Corte Feminino");
    }

    @Test
    @DisplayName("Should return empty list when no services in establishment")
    void shouldReturnEmptyList() {
        when(serviceRepository.findByEstablishmentId(establishmentId)).thenReturn(List.of());

        List<BeautyService> result = service.findByEstablishment(establishmentId);

        assertThat(result).isEmpty();
    }
}
