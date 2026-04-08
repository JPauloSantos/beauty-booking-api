package com.beautyscheduler.adapter.in.web.controller;

import com.beautyscheduler.adapter.in.web.dto.request.CreateBeautyServiceRequest;
import com.beautyscheduler.adapter.in.web.dto.response.BeautyServiceResponse;
import com.beautyscheduler.application.port.in.service.CreateBeautyServiceUseCase;
import com.beautyscheduler.application.port.in.service.GetBeautyServiceUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/services")
@Tag(name = "Beauty Services", description = "Service catalog management")
@SecurityRequirement(name = "bearerAuth")
public class BeautyServiceController {

    private final CreateBeautyServiceUseCase createUseCase;
    private final GetBeautyServiceUseCase getUseCase;

    public BeautyServiceController(CreateBeautyServiceUseCase createUseCase,
                                    GetBeautyServiceUseCase getUseCase) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('ESTABLISHMENT_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Create a new service")
    public ResponseEntity<BeautyServiceResponse> create(@Valid @RequestBody CreateBeautyServiceRequest request) {
        var service = createUseCase.execute(new CreateBeautyServiceUseCase.Command(
                request.name(), request.description(), request.durationMinutes(),
                request.price(), request.establishmentId()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(BeautyServiceResponse.from(service));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get service by ID")
    public ResponseEntity<BeautyServiceResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(BeautyServiceResponse.from(getUseCase.findById(id)));
    }

    @GetMapping("/establishment/{establishmentId}")
    @Operation(summary = "Get all services of an establishment")
    public ResponseEntity<List<BeautyServiceResponse>> getByEstablishment(@PathVariable UUID establishmentId) {
        List<BeautyServiceResponse> result = getUseCase.findByEstablishment(establishmentId)
                .stream().map(BeautyServiceResponse::from).toList();
        return ResponseEntity.ok(result);
    }
}
