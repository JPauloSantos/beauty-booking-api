package com.beautyscheduler.adapter.in.web.controller;

import com.beautyscheduler.adapter.in.web.dto.request.CreateProfessionalRequest;
import com.beautyscheduler.adapter.in.web.dto.response.ProfessionalResponse;
import com.beautyscheduler.application.port.in.professional.CreateProfessionalUseCase;
import com.beautyscheduler.application.port.in.professional.GetProfessionalUseCase;
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
@RequestMapping("/api/v1/professionals")
@Tag(name = "Professionals", description = "Professional profile management")
@SecurityRequirement(name = "bearerAuth")
public class ProfessionalController {

    private final CreateProfessionalUseCase createUseCase;
    private final GetProfessionalUseCase getUseCase;

    public ProfessionalController(CreateProfessionalUseCase createUseCase,
                                   GetProfessionalUseCase getUseCase) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('ESTABLISHMENT_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Create a professional profile")
    public ResponseEntity<ProfessionalResponse> create(@Valid @RequestBody CreateProfessionalRequest request) {
        var professional = createUseCase.execute(new CreateProfessionalUseCase.Command(
                request.name(), request.bio(), request.specialties(),
                request.establishmentId(), request.userId(), request.hourlyRate()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(ProfessionalResponse.from(professional));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get professional by ID")
    public ResponseEntity<ProfessionalResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ProfessionalResponse.from(getUseCase.findById(id)));
    }

    @GetMapping("/establishment/{establishmentId}")
    @Operation(summary = "Get all professionals of an establishment")
    public ResponseEntity<List<ProfessionalResponse>> getByEstablishment(@PathVariable UUID establishmentId) {
        List<ProfessionalResponse> result = getUseCase.findByEstablishment(establishmentId)
                .stream().map(ProfessionalResponse::from).toList();
        return ResponseEntity.ok(result);
    }
}
