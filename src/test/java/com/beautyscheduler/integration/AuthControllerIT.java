package com.beautyscheduler.integration;

import com.beautyscheduler.adapter.in.web.dto.request.LoginRequest;
import com.beautyscheduler.adapter.in.web.dto.request.RegisterUserRequest;
import com.beautyscheduler.domain.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("AuthController Integration Tests")
class AuthControllerIT {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    @DisplayName("Should register user and return 201")
    void shouldRegisterUser() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest(
                "Test User", "testuser@it.com", "password123",
                User.UserRole.CLIENT, "11988887777");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should login and return JWT token")
    void shouldLoginAndReturnToken() throws Exception {
        RegisterUserRequest register = new RegisterUserRequest(
                "Login User", "loginuser@it.com", "password123",
                User.UserRole.CLIENT, "11988887777");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)));

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new LoginRequest("loginuser@it.com", "password123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("loginuser@it.com"))
                .andExpect(jsonPath("$.role").value("CLIENT"));
    }

    @Test
    @DisplayName("Should return 403 on invalid credentials")
    void shouldReturn403OnInvalidCredentials() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new LoginRequest("nobody@it.com", "wrongpass"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 400 when registering with missing fields")
    void shouldReturn400WhenMissingFields() throws Exception {
        String invalidJson = "{\"email\":\"incomplete@it.com\"}";

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 422 when email is already registered")
    void shouldReturn422WhenEmailAlreadyRegistered() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest(
                "Dup User", "dup@it.com", "password123",
                User.UserRole.CLIENT, "11988887777");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }
}
