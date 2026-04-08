package com.beautyscheduler.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateEstablishmentRequest(
        @NotBlank(message = "Name is required") String name,
        String description,
        @NotBlank(message = "Street is required") String street,
        @NotBlank(message = "Number is required") String number,
        String complement,
        @NotBlank(message = "Neighborhood is required") String neighborhood,
        @NotBlank(message = "City is required") String city,
        @NotBlank(message = "State is required") String state,
        @NotBlank(message = "ZipCode is required") String zipCode,
        Double latitude,
        Double longitude
) {}
