package com.portafolio.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity // <--- Importante: Usamos la versión Reactiva de Security
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                // 1. Apagamos la protección CSRF (No la necesitamos porque usaremos JWT)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                // 2. Apagamos el formulario de login por defecto y la autenticación básica
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                // 3. Reglas de rutas (Por AHORA dejamos pasar todo para probar el enrutamiento)
                .authorizeExchange(auth -> auth
                        .anyExchange().permitAll()
                );

        return http.build();
    }
}