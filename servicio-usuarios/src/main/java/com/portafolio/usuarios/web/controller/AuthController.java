package com.portafolio.usuarios.web.controller;

import com.portafolio.usuarios.application.dto.AuthenticateUserCommand;
import com.portafolio.usuarios.application.usecase.AuthenticateUserUseCase;
import com.portafolio.usuarios.domain.entity.User;
import com.portafolio.usuarios.infrastructure.security.JwtUtils;
import com.portafolio.usuarios.web.dto.AuthResponse;
import com.portafolio.usuarios.web.dto.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticateUserUseCase authenticateUserUseCase, JwtUtils jwtUtils) {
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthenticateUserCommand command = new AuthenticateUserCommand(request.email(), request.password());

        User user = authenticateUserUseCase.execute(command);

        String token = jwtUtils.generateAccessToken(user.getEmail(), user.getId(), user.getRole());

        return ResponseEntity.ok(new AuthResponse(token, user.getEmail(), user.getId()));
    }
}