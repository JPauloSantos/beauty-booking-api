package com.beautyscheduler.adapter.in.web.dto.request;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record CreateReviewRequest(
        @NotNull UUID establishmentId,
        UUID professionalId,
        @NotNull UUID appointmentId,
        @NotNull @Min(1) @Max(5) int rating,
        @Size(max = 1000) String comment
) {}
