package com.beautyscheduler.adapter.in.web.controller;

import com.beautyscheduler.adapter.in.web.dto.request.CreateReviewRequest;
import com.beautyscheduler.adapter.in.web.dto.response.ReviewResponse;
import com.beautyscheduler.application.port.in.review.CreateReviewUseCase;
import com.beautyscheduler.application.port.in.review.GetReviewUseCase;
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
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@Tag(name = "Reviews", description = "Post-service evaluations")
@SecurityRequirement(name = "bearerAuth")
public class ReviewController {

    private final CreateReviewUseCase createUseCase;
    private final GetReviewUseCase getUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    public ReviewController(CreateReviewUseCase createUseCase,
                             GetReviewUseCase getUseCase,
                             JwtTokenProvider jwtTokenProvider) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping
    @Operation(summary = "Submit a review for a completed appointment")
    public ResponseEntity<ReviewResponse> create(
            @Valid @RequestBody CreateReviewRequest request,
            HttpServletRequest httpRequest) {
        UUID clientId = extractUserId(httpRequest);
        var review = createUseCase.execute(new CreateReviewUseCase.Command(
                clientId, request.establishmentId(), request.professionalId(),
                request.appointmentId(), request.rating(), request.comment()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(ReviewResponse.from(review));
    }

    @GetMapping("/establishment/{establishmentId}")
    @Operation(summary = "Get reviews for an establishment")
    public ResponseEntity<List<ReviewResponse>> getByEstablishment(@PathVariable UUID establishmentId) {
        List<ReviewResponse> result = getUseCase.findByEstablishment(establishmentId)
                .stream().map(ReviewResponse::from).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/establishment/{establishmentId}/average")
    @Operation(summary = "Get average rating for an establishment")
    public ResponseEntity<Map<String, Double>> getAverageRating(@PathVariable UUID establishmentId) {
        double avg = getUseCase.averageRatingForEstablishment(establishmentId);
        return ResponseEntity.ok(Map.of("averageRating", avg));
    }

    @GetMapping("/professional/{professionalId}")
    @Operation(summary = "Get reviews for a professional")
    public ResponseEntity<List<ReviewResponse>> getByProfessional(@PathVariable UUID professionalId) {
        List<ReviewResponse> result = getUseCase.findByProfessional(professionalId)
                .stream().map(ReviewResponse::from).toList();
        return ResponseEntity.ok(result);
    }

    private UUID extractUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return UUID.fromString(jwtTokenProvider.getSubjectId(token));
    }
}
