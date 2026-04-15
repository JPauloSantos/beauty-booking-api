package com.beautyscheduler.integration;

import com.beautyscheduler.adapter.in.web.dto.request.CreateAppointmentRequest;
import com.beautyscheduler.adapter.in.web.dto.request.CreateBeautyServiceRequest;
import com.beautyscheduler.adapter.in.web.dto.request.CreateEstablishmentRequest;
import com.beautyscheduler.adapter.in.web.dto.request.CreateProfessionalRequest;
import com.beautyscheduler.adapter.in.web.dto.request.LoginRequest;
import com.beautyscheduler.adapter.in.web.dto.request.RegisterUserRequest;
import com.beautyscheduler.adapter.in.web.dto.request.RescheduleAppointmentRequest;
import com.beautyscheduler.adapter.in.web.dto.response.AppointmentResponse;
import com.beautyscheduler.adapter.in.web.dto.response.AuthResponse;
import com.beautyscheduler.adapter.in.web.dto.response.EstablishmentResponse;
import com.beautyscheduler.adapter.in.web.dto.response.ProfessionalResponse;
import com.beautyscheduler.adapter.in.web.dto.response.BeautyServiceResponse;
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
@DisplayName("AppointmentController Integration Tests")
class AppointmentControllerIT {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private String clientToken;
    private String ownerToken;
    private UUID establishmentId;
    private UUID professionalId;
    private UUID serviceId;

    @BeforeEach
    void setUp() throws Exception {
        // Register and login as owner
        String ownerEmail = "owner-apt-" + UUID.randomUUID() + "@it.com";
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RegisterUserRequest(
                        "Owner", ownerEmail, "password123",
                        User.UserRole.ESTABLISHMENT_OWNER, "11999999999"))));

        MvcResult ownerLogin = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest(ownerEmail, "password123"))))
                .andExpect(status().isOk()).andReturn();
        ownerToken = objectMapper.readValue(
                ownerLogin.getResponse().getContentAsString(), AuthResponse.class).token();

        // Register and login as client
        String clientEmail = "client-apt-" + UUID.randomUUID() + "@it.com";
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

        // Create establishment
        MvcResult estResult = mockMvc.perform(post("/api/v1/establishments")
                .header("Authorization", "Bearer " + ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateEstablishmentRequest(
                        "Salão IT", "Desc", "Rua A", "1", null,
                        "Centro", "São Paulo", "SP", "01310-100", null, null))))
                .andExpect(status().isCreated()).andReturn();
        establishmentId = objectMapper.readValue(
                estResult.getResponse().getContentAsString(), EstablishmentResponse.class).id();

        // Create professional
        MvcResult profResult = mockMvc.perform(post("/api/v1/professionals")
                .header("Authorization", "Bearer " + ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateProfessionalRequest(
                        "Prof IT", "bio", List.of("Corte"), establishmentId, null, BigDecimal.valueOf(80)))))
                .andExpect(status().isCreated()).andReturn();
        professionalId = objectMapper.readValue(
                profResult.getResponse().getContentAsString(), ProfessionalResponse.class).id();

        // Create service
        MvcResult svcResult = mockMvc.perform(post("/api/v1/services")
                .header("Authorization", "Bearer " + ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateBeautyServiceRequest(
                        "Corte IT", "desc", 60, BigDecimal.valueOf(50), establishmentId))))
                .andExpect(status().isCreated()).andReturn();
        serviceId = objectMapper.readValue(
                svcResult.getResponse().getContentAsString(), BeautyServiceResponse.class).id();
    }

    @Test
    @DisplayName("Should create appointment successfully")
    void shouldCreateAppointment() throws Exception {
        CreateAppointmentRequest request = new CreateAppointmentRequest(
                professionalId, serviceId, establishmentId,
                LocalDateTime.now().plusDays(2), "No notes");

        mockMvc.perform(post("/api/v1/appointments")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("Should return 401 when creating appointment without token")
    void shouldReturn401WithoutToken() throws Exception {
        CreateAppointmentRequest request = new CreateAppointmentRequest(
                professionalId, serviceId, establishmentId,
                LocalDateTime.now().plusDays(2), null);

        mockMvc.perform(post("/api/v1/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should list my appointments")
    void shouldListMyAppointments() throws Exception {
        // Create one appointment first
        mockMvc.perform(post("/api/v1/appointments")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateAppointmentRequest(
                        professionalId, serviceId, establishmentId,
                        LocalDateTime.now().plusDays(3), null))));

        mockMvc.perform(get("/api/v1/appointments/my")
                .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should cancel appointment")
    void shouldCancelAppointment() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/v1/appointments")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateAppointmentRequest(
                        professionalId, serviceId, establishmentId,
                        LocalDateTime.now().plusDays(4), null))))
                .andExpect(status().isCreated()).andReturn();

        UUID apptId = objectMapper.readValue(
                created.getResponse().getContentAsString(), AppointmentResponse.class).id();

        mockMvc.perform(patch("/api/v1/appointments/" + apptId + "/cancel")
                .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return 400 when scheduling in the past")
    void shouldReturn400WhenSchedulingInPast() throws Exception {
        CreateAppointmentRequest request = new CreateAppointmentRequest(
                professionalId, serviceId, establishmentId,
                LocalDateTime.now().minusDays(1), null);

        mockMvc.perform(post("/api/v1/appointments")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get appointment by ID")
    void shouldGetAppointmentById() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/v1/appointments")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateAppointmentRequest(
                        professionalId, serviceId, establishmentId,
                        LocalDateTime.now().plusDays(5), null))))
                .andExpect(status().isCreated()).andReturn();

        UUID apptId = objectMapper.readValue(
                created.getResponse().getContentAsString(), AppointmentResponse.class).id();

        mockMvc.perform(get("/api/v1/appointments/" + apptId)
                .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("Should get appointments by establishment")
    void shouldGetAppointmentsByEstablishment() throws Exception {
        mockMvc.perform(post("/api/v1/appointments")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateAppointmentRequest(
                        professionalId, serviceId, establishmentId,
                        LocalDateTime.now().plusDays(6), null))));

        mockMvc.perform(get("/api/v1/appointments/establishment/" + establishmentId)
                .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should reschedule appointment")
    void shouldRescheduleAppointment() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/v1/appointments")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateAppointmentRequest(
                        professionalId, serviceId, establishmentId,
                        LocalDateTime.now().plusDays(7), null))))
                .andExpect(status().isCreated()).andReturn();

        UUID apptId = objectMapper.readValue(
                created.getResponse().getContentAsString(), AppointmentResponse.class).id();

        mockMvc.perform(patch("/api/v1/appointments/" + apptId + "/reschedule")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new RescheduleAppointmentRequest(LocalDateTime.now().plusDays(14)))))
                .andExpect(status().isOk());
    }
}
