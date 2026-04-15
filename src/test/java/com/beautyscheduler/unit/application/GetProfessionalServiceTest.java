package com.beautyscheduler.unit.application;

import com.beautyscheduler.application.port.out.ProfessionalRepositoryPort;
import com.beautyscheduler.application.usecase.professional.GetProfessionalService;
import com.beautyscheduler.domain.entity.Professional;
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
@DisplayName("GetProfessionalService Unit Tests")
class GetProfessionalServiceTest {

    @Mock private ProfessionalRepositoryPort professionalRepository;

    private GetProfessionalService service;

    private UUID establishmentId;
    private Professional professional;

    @BeforeEach
    void setUp() {
        service = new GetProfessionalService(professionalRepository);
        establishmentId = UUID.randomUUID();
        professional = Professional.create("Ana Silva", "Expert stylist",
                List.of("Corte", "Coloração"), establishmentId, null, BigDecimal.valueOf(100));
    }

    @Test
    @DisplayName("Should return professional when found by ID")
    void shouldFindById() {
        UUID id = professional.getId();
        when(professionalRepository.findById(id)).thenReturn(Optional.of(professional));

        Professional result = service.findById(id);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Ana Silva");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when professional not found")
    void shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(professionalRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should return list of professionals by establishment")
    void shouldFindByEstablishment() {
        when(professionalRepository.findByEstablishmentId(establishmentId)).thenReturn(List.of(professional));

        List<Professional> result = service.findByEstablishment(establishmentId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Ana Silva");
    }

    @Test
    @DisplayName("Should return empty list when no professionals in establishment")
    void shouldReturnEmptyList() {
        when(professionalRepository.findByEstablishmentId(establishmentId)).thenReturn(List.of());

        List<Professional> result = service.findByEstablishment(establishmentId);

        assertThat(result).isEmpty();
    }
}
