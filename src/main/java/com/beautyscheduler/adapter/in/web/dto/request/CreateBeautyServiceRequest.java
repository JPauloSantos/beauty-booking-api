package com.beautyscheduler.adapter.in.web.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateBeautyServiceRequest(
        @NotBlank(message = "Name is required") String name,
        String description,
        @Positive(message = "Duration must be positive") int durationMinutes,
        @NotNull @Positive BigDecimal price,
        @NotNull UUID establishmentId
) {}
