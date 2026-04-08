package com.beautyscheduler.adapter.in.web.controller;

import com.beautyscheduler.adapter.in.web.dto.request.CreateEstablishmentRequest;
import com.beautyscheduler.adapter.in.web.dto.response.EstablishmentResponse;
import com.beautyscheduler.application.port.in.establishment.CreateEstablishmentUseCase;
import com.beautyscheduler.application.port.in.establishment.GetEstablishmentUseCase;
import com.beautyscheduler.application.port.in.establishment.SearchEstablishmentUseCase;
import com.beautyscheduler.application.port.out.StoragePort;
import com.beautyscheduler.domain.entity.Establishment;
import com.beautyscheduler.domain.exception.ResourceNotFoundException;
import com.beautyscheduler.domain.valueobject.Address;
import com.beautyscheduler.infrastructure.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/establishments")
@Tag(name = "Establishments", description = "Establishment management")
@SecurityRequirement(name = "bearerAuth")
public class EstablishmentController {

    private final CreateEstablishmentUseCase createUseCase;
    private final GetEstablishmentUseCase getUseCase;
    private final SearchEstablishmentUseCase searchUseCase;
    private final StoragePort storagePort;
    private final JwtTokenProvider jwtTokenProvider;

    public EstablishmentController(CreateEstablishmentUseCase createUseCase,
                                    GetEstablishmentUseCase getUseCase,
                                    SearchEstablishmentUseCase searchUseCase,
                                    StoragePort storagePort,
                                    JwtTokenProvider jwtTokenProvider) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
        this.searchUseCase = searchUseCase;
        this.storagePort = storagePort;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping
    @PreAuthorize("hasRole('ESTABLISHMENT_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Create a new establishment")
    public ResponseEntity<EstablishmentResponse> create(
            @Valid @RequestBody CreateEstablishmentRequest request,
            HttpServletRequest httpRequest) {
        UUID ownerId = extractUserId(httpRequest);
        Address address = new Address(request.street(), request.number(), request.complement(),
                request.neighborhood(), request.city(), request.state(),
                request.zipCode(), request.latitude(), request.longitude());

        Establishment created = createUseCase.execute(
                new CreateEstablishmentUseCase.Command(request.name(), request.description(), address, ownerId));
        return ResponseEntity.status(HttpStatus.CREATED).body(EstablishmentResponse.from(created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get establishment by ID")
    public ResponseEntity<EstablishmentResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(EstablishmentResponse.from(getUseCase.findById(id)));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('ESTABLISHMENT_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Get establishments owned by current user")
    public ResponseEntity<List<EstablishmentResponse>> getMyEstablishments(HttpServletRequest request) {
        UUID ownerId = extractUserId(request);
        List<EstablishmentResponse> result = getUseCase.findByOwner(ownerId)
                .stream().map(EstablishmentResponse::from).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    @Operation(summary = "Search establishments with filters")
    public ResponseEntity<List<EstablishmentResponse>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String service,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        List<EstablishmentResponse> result = searchUseCase.search(
                new SearchEstablishmentUseCase.Filters(name, city, service, minRating, minPrice, maxPrice))
                .stream().map(EstablishmentResponse::from).toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/{id}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ESTABLISHMENT_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Upload a photo for an establishment")
    public ResponseEntity<EstablishmentResponse> uploadPhoto(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) throws IOException {
        Establishment establishment = getUseCase.findById(id);
        String photoUrl = storagePort.upload(file.getInputStream(), file.getOriginalFilename(), "establishments");
        establishment.addPhoto(photoUrl);
        return ResponseEntity.ok(EstablishmentResponse.from(establishment));
    }

    private UUID extractUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return UUID.fromString(jwtTokenProvider.getSubjectId(token));
    }
}
