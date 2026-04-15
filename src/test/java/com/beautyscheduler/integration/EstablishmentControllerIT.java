package com.beautyscheduler.integration;

import com.beautyscheduler.adapter.in.web.dto.request.CreateEstablishmentRequest;
import com.beautyscheduler.adapter.in.web.dto.request.LoginRequest;
import com.beautyscheduler.adapter.in.web.dto.request.RegisterUserRequest;
import com.beautyscheduler.adapter.in.web.dto.response.AuthResponse;
import com.beautyscheduler.adapter.in.web.dto.response.EstablishmentResponse;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("EstablishmentController Integration Tests")
class EstablishmentControllerIT {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private String ownerToken;

    @BeforeEach
    void setUp() throws Exception {
        RegisterUserRequest register = new RegisterUserRequest(
                "Owner", "owner@test.com", "password123",
                User.UserRole.ESTABLISHMENT_OWNER, "11999999999");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)));

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new LoginRequest("owner@test.com", "password123"))))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(), AuthResponse.class);
        ownerToken = authResponse.token();
    }

    @Test
    @DisplayName("Should create establishment successfully")
    void shouldCreateEstablishment() throws Exception {
        CreateEstablishmentRequest request = new CreateEstablishmentRequest(
                "Salão Bela Vista", "Salão completo",
                "Rua das Flores", "123", null, "Centro",
                "São Paulo", "SP", "01310-100", -23.56, -46.63);

        mockMvc.perform(post("/api/v1/establishments")
                .header("Authorization", "Bearer " + ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Salão Bela Vista"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("Should return 401 when creating without token")
    void shouldReturn401WithoutToken() throws Exception {
        CreateEstablishmentRequest request = new CreateEstablishmentRequest(
                "Test", null, "Street", "1", null, "Neighborhood",
                "City", "SP", "00000-000", null, null);

        mockMvc.perform(post("/api/v1/establishments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should search establishments publicly")
    void shouldSearchEstablishmentsPublicly() throws Exception {
        mockMvc.perform(get("/api/v1/establishments/search")
                .param("city", "São Paulo"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 400 when creating with missing required fields")
    void shouldReturn400WhenMissingFields() throws Exception {
        String invalidJson = "{\"description\":\"no name\"}";

        mockMvc.perform(post("/api/v1/establishments")
                .header("Authorization", "Bearer " + ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get establishment by ID")
    void shouldGetEstablishmentById() throws Exception {
        CreateEstablishmentRequest request = new CreateEstablishmentRequest(
                "Salão Get", "Desc", "Rua B", "10", null, "Centro",
                "Campinas", "SP", "13010-000", null, null);

        MvcResult created = mockMvc.perform(post("/api/v1/establishments")
                .header("Authorization", "Bearer " + ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()).andReturn();

        UUID id = objectMapper.readValue(
                created.getResponse().getContentAsString(), EstablishmentResponse.class).id();

        mockMvc.perform(get("/api/v1/establishments/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Salão Get"));
    }

    @Test
    @DisplayName("Should return 404 when establishment not found")
    void shouldReturn404WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/establishments/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get my establishments")
    void shouldGetMyEstablishments() throws Exception {
        CreateEstablishmentRequest request = new CreateEstablishmentRequest(
                "Meu Salão", "Desc", "Rua C", "20", null, "Bairro",
                "Rio de Janeiro", "RJ", "20000-000", null, null);

        mockMvc.perform(post("/api/v1/establishments")
                .header("Authorization", "Bearer " + ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(get("/api/v1/establishments/my")
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
