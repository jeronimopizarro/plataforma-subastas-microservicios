package com.portafolio.gateway.web;

import com.portafolio.gateway.security.JwtUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtils jwtUtils;

    public AuthController(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    // Simulamos un login. Le pasamos un ID de usuario por parámetro y nos devuelve un Token real.
    @PostMapping("/login")
    public Map<String, String> loginMock(@RequestParam String userId) {
        String token = jwtUtils.generateJwtToken(userId);
        return Map.of(
                "mensaje", "Login exitoso (Mock)",
                "token", token
        );
    }
}