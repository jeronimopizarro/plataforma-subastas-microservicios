package com.portafolio.gateway.web;

import com.portafolio.gateway.security.JwtUtils;
import com.portafolio.gateway.web.dto.AuthResponse;
import com.portafolio.gateway.web.dto.LoginRequest;
import com.portafolio.gateway.web.dto.RefreshRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtils jwtUtils;
    // Aquí inyectarías tu servicio de usuarios si necesitas validar que siga activo en BD
    // private final UserService userService;

    public AuthController(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    // El endpoint de Login tradicional ahora debe devolver AMBOS tokens
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // ... Tu lógica de autenticación de credenciales existente ...

        // Al tener éxito:
        String accessToken = jwtUtils.generateAccessToken(request.username(), new HashMap<>());
        String refreshToken = jwtUtils.generateRefreshToken(request.username());

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken, request.username()));
    }

    // --- NUEVO ENDPOINT DE RENOVACIÓN ---
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        try {
            String token = request.refreshToken();
            String username = jwtUtils.extractUsername(token);

            // Validamos que el Refresh Token sea legítimo y no esté vencido
            if (username != null && jwtUtils.validateToken(token, username)) {

                // Opcional: Podrías consultar a Redis o BD si el token fue revocado manualmente

                // Generamos un nuevo Access Token corto
                String newAccessToken = jwtUtils.generateAccessToken(username, new HashMap<>());

                // Buenas prácticas: También podés generar un nuevo Refresh Token (Token Rotation)
                String newRefreshToken = jwtUtils.generateRefreshToken(username);

                return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken, username));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("El Refresh Token es inválido o ha expirado.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error al procesar la renovación del token.");
        }
    }
}