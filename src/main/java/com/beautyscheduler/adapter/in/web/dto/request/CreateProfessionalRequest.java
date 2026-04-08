package com.beautyscheduler.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateProfessionalRequest(
        @NotBlank(message = "Name is required") String name,
        String bio,
        List<String> specialties,
        @NotNull(message = "EstablishmentId is required") UUID establishmentId,
        UUID userId,
        @NotNull @Positive BigDecimal hourlyRate
) {}
