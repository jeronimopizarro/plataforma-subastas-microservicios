package com.portafolio.gateway.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();

        // Simulamos la inyección del application.yml
        String secretDePrueba = "esta_es_una_clave_secreta_muy_larga_para_testear_jwt_123456789";
        ReflectionTestUtils.setField(jwtUtils, "secret", secretDePrueba);
    }

    @Test
    @DisplayName("El utilitario de JWT se inicializa correctamente con su secreto")
    void setup_Correcto() {
        assertNotNull(jwtUtils);
    }

    @Test
    @DisplayName("Debe generar un Access Token y un Refresh Token no vacíos")
    void generarTokens_Correcto() {
        String username = "usuario_test_123";
        Map<String, Object> claimsVacios = new HashMap<>();

        String accessToken = jwtUtils.generateAccessToken(username, claimsVacios);
        String refreshToken = jwtUtils.generateRefreshToken(username);

        // Verificamos que los tokens existan
        assertNotNull(accessToken, "El Access Token no debería ser nulo");
        assertNotNull(refreshToken, "El Refresh Token no debería ser nulo");

        // Verificamos que no sean textos vacíos
        org.junit.jupiter.api.Assertions.assertFalse(accessToken.isEmpty(), "El Access Token no debería estar vacío");
        org.junit.jupiter.api.Assertions.assertFalse(refreshToken.isEmpty(), "El Refresh Token no debería estar vacío");

        // Imprimimos por consola solo para verlos con nuestros propios ojos (opcional)
        System.out.println("Access Token generado: " + accessToken);
        System.out.println("Refresh Token generado: " + refreshToken);
    }

    @Test
    @DisplayName("Debe extraer el username correctamente y validar un token intacto")
    void leerYValidarToken_Correcto() {
        String usernameOriginal = "admin_subastas_99";
        String token = jwtUtils.generateAccessToken(usernameOriginal, new HashMap<>());

        // Extraemos el nombre directamente
        String usernameExtraido = jwtUtils.extractUsername(token);

        // Comprobamos la validación completa (la que usa el AuthController)
        boolean esValidoCompleto = jwtUtils.validateToken(token, usernameOriginal);

        // Comprobamos la validación del Filtro del Gateway (solo firma y expiración)
        boolean esValidoGateway = jwtUtils.validateJwtToken(token);

        // 3. Comprobación
        org.junit.jupiter.api.Assertions.assertEquals(
                usernameOriginal,
                usernameExtraido,
                "El username extraído debe ser exactamente igual al que guardamos"
        );
        org.junit.jupiter.api.Assertions.assertTrue(esValidoCompleto, "El token debe considerarse válido para este usuario");
        org.junit.jupiter.api.Assertions.assertTrue(esValidoGateway, "El token debe pasar la seguridad del filtro del Gateway");
    }

    @Test
    @DisplayName("Debe rechazar de inmediato tokens inventados o alterados")
    void validarToken_FalsoOAlterado() {
        // Generamos un token 100% válido y real
        String tokenReal = jwtUtils.generateAccessToken("usuario_legitimo", new HashMap<>());

        // le cambiamos la última letra de la firma
        String tokenAlterado = tokenReal.substring(0, tokenReal.length() - 1) + "X";

        // no es un JWT real
        String tokenBasura = "eyJhbGciOiJIUzI1NiJ9.hacker_payload.firma_inventada_123";

        // Ejecución
        boolean alteradoEsValido = jwtUtils.validateJwtToken(tokenAlterado);
        boolean basuraEsValida = jwtUtils.validateJwtToken(tokenBasura);

        // Comprobación
        org.junit.jupiter.api.Assertions.assertFalse(
                alteradoEsValido,
                "Un token al que se le alteró un solo carácter DEBE ser rechazado (Firma inválida)"
        );
        org.junit.jupiter.api.Assertions.assertFalse(
                basuraEsValida,
                "Un texto que imita ser un token DEBE ser rechazado"
        );
    }
}