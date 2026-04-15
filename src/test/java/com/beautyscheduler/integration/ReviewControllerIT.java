package com.beautyscheduler.integration;

import com.beautyscheduler.adapter.in.web.dto.request.*;
import com.beautyscheduler.adapter.in.web.dto.response.*;
import com.beautyscheduler.domain.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("ReviewController Integration Tests")
class ReviewControllerIT {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private String clientToken;
    private UUID establishmentId;
    private UUID professionalId;

    @BeforeEach
    void setUp() throws Exception {
        String ownerEmail = "owner-rev-" + UUID.randomUUID() + "@it.com";
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RegisterUserRequest(
                        "Owner", ownerEmail, "password123",
                        User.UserRole.ESTABLISHMENT_OWNER, "11999999999"))));

        MvcResult ownerLogin = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest(ownerEmail, "password123"))))
                .andExpect(status().isOk()).andReturn();
        String ownerToken = objectMapper.readValue(
                ownerLogin.getResponse().getContentAsString(), AuthResponse.class).token();

        String clientEmail = "client-rev-" + UUID.randomUUID() + "@it.com";
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RegisterUserRequest(
                        "Client", clientEmail, "password123",
                        User.UserRole.CLIENT, "11999999999"))));

        MvcResult clientLogin = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest(clientEmail, "password123"))))
                .andExpect(status().isOk()).andReturn();
        clientToken = objectMapper.readValue(
                clientLogin.getResponse().getContentAsString(), AuthResponse.class).token();

        MvcResult estResult = mockMvc.perform(post("/api/v1/establishments")
                .header("Authorization", "Bearer " + ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateEstablishmentRequest(
                        "Salão Rev", "Desc", "Rua Rev", "1", null,
                        "Centro", "São Paulo", "SP", "01310-100", null, null))))
                .andExpect(status().isCreated()).andReturn();
        establishmentId = objectMapper.readValue(
                estResult.getResponse().getContentAsString(), EstablishmentResponse.class).id();

        MvcResult profResult = mockMvc.perform(post("/api/v1/professionals")
                .header("Authorization", "Bearer " + ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateProfessionalRequest(
                        "Prof Rev", "bio", List.of("Corte"), establishmentId, null, BigDecimal.valueOf(80)))))
                .andExpect(status().isCreated()).andReturn();
        professionalId = objectMapper.readValue(
                profResult.getResponse().getContentAsString(), ProfessionalResponse.class).id();

        MvcResult svcResult = mockMvc.perform(post("/api/v1/services")
                .header("Authorization", "Bearer " + ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateBeautyServiceRequest(
                        "Corte Rev", "desc", 60, BigDecimal.valueOf(50), establishmentId))))
                .andExpect(status().isCreated()).andReturn();
        UUID serviceId = objectMapper.readValue(
                svcResult.getResponse().getContentAsString(), BeautyServiceResponse.class).id();

        mockMvc.perform(post("/api/v1/appointments")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateAppointmentRequest(
                        professionalId, serviceId, establishmentId,
                        LocalDateTime.now().plusDays(2), null))))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should get empty reviews list for establishment")
    void shouldGetReviewsByEstablishment() throws Exception {
        mockMvc.perform(get("/api/v1/reviews/establishment/" + establishmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should get average rating for establishment")
    void shouldGetAverageRating() throws Exception {
        mockMvc.perform(get("/api/v1/reviews/establishment/" + establishmentId + "/average"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").exists());
    }

    @Test
    @DisplayName("Should get reviews by professional")
    void shouldGetReviewsByProfessional() throws Exception {
        mockMvc.perform(get("/api/v1/reviews/professional/" + professionalId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should get services by establishment")
    void shouldGetServicesByEstablishment() throws Exception {
        mockMvc.perform(get("/api/v1/services/establishment/" + establishmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should get professionals by establishment")
    void shouldGetProfessionalsByEstablishment() throws Exception {
        mockMvc.perform(get("/api/v1/professionals/establishment/" + establishmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should get professional by ID")
    void shouldGetProfessionalById() throws Exception {
        mockMvc.perform(get("/api/v1/professionals/" + professionalId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Prof Rev"));
    }
}
