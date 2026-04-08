package com.beautyscheduler.adapter.in.web.controller;

import com.beautyscheduler.adapter.in.web.dto.request.CreateAppointmentRequest;
import com.beautyscheduler.adapter.in.web.dto.request.RescheduleAppointmentRequest;
import com.beautyscheduler.adapter.in.web.dto.response.AppointmentResponse;
import com.beautyscheduler.application.port.in.appointment.*;
import com.beautyscheduler.infrastructure.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/appointments")
@Tag(name = "Appointments", description = "Appointment scheduling and management")
@SecurityRequirement(name = "bearerAuth")
public class AppointmentController {

    private final CreateAppointmentUseCase createUseCase;
    private final CancelAppointmentUseCase cancelUseCase;
    private final RescheduleAppointmentUseCase rescheduleUseCase;
    private final GetAppointmentUseCase getUseCase;
    private final MarkNoShowUseCase markNoShowUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    public AppointmentController(CreateAppointmentUseCase createUseCase,
                                  CancelAppointmentUseCase cancelUseCase,
                                  RescheduleAppointmentUseCase rescheduleUseCase,
                                  GetAppointmentUseCase getUseCase,
                                  MarkNoShowUseCase markNoShowUseCase,
                                  JwtTokenProvider jwtTokenProvider) {
        this.createUseCase = createUseCase;
        this.cancelUseCase = cancelUseCase;
        this.rescheduleUseCase = rescheduleUseCase;
        this.getUseCase = getUseCase;
        this.markNoShowUseCase = markNoShowUseCase;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping
    @Operation(summary = "Book a new appointment")
    public ResponseEntity<AppointmentResponse> create(
            @Valid @RequestBody CreateAppointmentRequest request,
            HttpServletRequest httpRequest) {
        UUID clientId = extractUserId(httpRequest);
        var appointment = createUseCase.execute(new CreateAppointmentUseCase.Command(
                clientId, request.professionalId(), request.serviceId(),
                request.establishmentId(), request.scheduledAt(), request.clientNotes()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(AppointmentResponse.from(appointment));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment by ID")
    public ResponseEntity<AppointmentResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(AppointmentResponse.from(getUseCase.findById(id)));
    }

    @GetMapping("/my")
    @Operation(summary = "Get appointments for current user (as client)")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(HttpServletRequest request) {
        UUID clientId = extractUserId(request);
        List<AppointmentResponse> result = getUseCase.findByClient(clientId)
                .stream().map(AppointmentResponse::from).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/establishment/{establishmentId}")
    @Operation(summary = "Get all appointments of an establishment")
    public ResponseEntity<List<AppointmentResponse>> getByEstablishment(@PathVariable UUID establishmentId) {
        List<AppointmentResponse> result = getUseCase.findByEstablishment(establishmentId)
                .stream().map(AppointmentResponse::from).toList();
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel an appointment")
    public ResponseEntity<Void> cancel(@PathVariable UUID id, HttpServletRequest request) {
        UUID requesterId = extractUserId(request);
        cancelUseCase.execute(id, requesterId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reschedule")
    @Operation(summary = "Reschedule an appointment")
    public ResponseEntity<AppointmentResponse> reschedule(
            @PathVariable UUID id,
            @Valid @RequestBody RescheduleAppointmentRequest request,
            HttpServletRequest httpRequest) {
        UUID requesterId = extractUserId(httpRequest);
        var updated = rescheduleUseCase.execute(new RescheduleAppointmentUseCase.Command(
                id, requesterId, request.newScheduledAt()));
        return ResponseEntity.ok(AppointmentResponse.from(updated));
    }

    @PatchMapping("/{id}/no-show")
    @Operation(summary = "Mark appointment as no-show")
    public ResponseEntity<Void> markNoShow(@PathVariable UUID id, HttpServletRequest request) {
        UUID requesterId = extractUserId(request);
        markNoShowUseCase.execute(id, requesterId);
        return ResponseEntity.noContent().build();
    }

    private UUID extractUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return UUID.fromString(jwtTokenProvider.getSubjectId(token));
    }
}
