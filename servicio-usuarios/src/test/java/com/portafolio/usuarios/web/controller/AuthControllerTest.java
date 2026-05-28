package com.portafolio.usuarios.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portafolio.usuarios.application.dto.AuthenticateUserCommand;
import com.portafolio.usuarios.application.dto.RegisterUserCommand;
import com.portafolio.usuarios.application.usecase.AuthenticateUserUseCase;
import com.portafolio.usuarios.application.usecase.RegisterUserUseCase;
import com.portafolio.usuarios.domain.entity.User;
import com.portafolio.usuarios.infrastructure.security.JwtUtils;
import com.portafolio.usuarios.web.dto.LoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticateUserUseCase authenticateUserUseCase;

    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Test
    @DisplayName("POST /auth/register - Debería registrar un usuario y retornar 201")
    void shouldRegisterUserAndReturn201() throws Exception {
        RegisterUserCommand command = new RegisterUserCommand("test@test.com", "pass", "USER");
        User mockUser = User.restore(1L, "test@test.com", "pass", "USER");

        when(registerUserUseCase.execute(any(RegisterUserCommand.class))).thenReturn(mockUser);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Usuario registrado con éxito"));
    }

    @Test
    @DisplayName("POST /auth/login - Debería autenticar y retornar el token JWT")
    void shouldLoginAndReturnToken() throws Exception {
        LoginRequest request = new LoginRequest("test@test.com", "pass");
        User mockUser = User.restore(1L, "test@test.com", "pass", "USER");

        when(authenticateUserUseCase.execute(any(AuthenticateUserCommand.class))).thenReturn(mockUser);
        when(jwtUtils.generateAccessToken("test@test.com", 1L, "USER")).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }
}