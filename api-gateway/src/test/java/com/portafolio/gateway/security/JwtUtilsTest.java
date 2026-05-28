package com.portafolio.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private final String secretDePrueba = "esta_es_una_clave_secreta_muy_larga_para_testear_jwt_123456789";

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();

        // El campo en el nuevo JwtUtils del Gateway se llama "secretKey"
        ReflectionTestUtils.setField(jwtUtils, "secretKey", secretDePrueba);
    }

    /**
     * Método auxiliar interno: Como el Gateway ya no fabrica tokens,
     * fabricamos uno manualmente aquí solo para poder testear los métodos de lectura.
     */
    private String fabricarTokenDePrueba(String email, Long userId, String role) {
        SecretKey key = Keys.hmacShaKeyFor(secretDePrueba.getBytes());
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 900000))
                .signWith(key)
                .compact();
    }

    @Test
    @DisplayName("El utilitario de JWT se inicializa correctamente con su secreto")
    void setup_Correcto() {
        assertNotNull(jwtUtils);
    }

    @Test
    @DisplayName("Debe extraer el subject y los claims (userId, role) correctamente")
    void extraerClaims_Correcto() {
        // Arrange
        String emailOriginal = "admin@subastas.com";
        Long userIdOriginal = 1L;
        String rolOriginal = "USER";

        String token = fabricarTokenDePrueba(emailOriginal, userIdOriginal, rolOriginal);

        // Act
        Claims claims = jwtUtils.getAllClaimsFromToken(token);

        // Assert
        assertEquals(emailOriginal, claims.getSubject(), "El subject debe coincidir con el correo");
        // JJWT suele parsear los números pequeños como Integer, por eso lo pasamos a String para comparar seguro
        assertEquals(String.valueOf(userIdOriginal), claims.get("userId").toString(), "El userId debe coincidir");
        assertEquals(rolOriginal, claims.get("role"), "El rol debe coincidir");
    }

    @Test
    @DisplayName("Debe validar correctamente un token intacto")
    void validarToken_Correcto() {
        // Arrange
        String token = fabricarTokenDePrueba("test@subastas.com", 2L, "USER");

        // Act
        boolean esValido = jwtUtils.isTokenValid(token);

        // Assert
        assertTrue(esValido, "El token generado con el mismo secreto debe considerarse válido");
    }

    @Test
    @DisplayName("Debe rechazar de inmediato tokens inventados o alterados")
    void validarToken_FalsoOAlterado() {
        // Arrange
        String tokenReal = fabricarTokenDePrueba("usuario@legitimo.com", 99L, "USER");

        // Le cambiamos la última letra de la firma para corromperlo
        String tokenAlterado = tokenReal.substring(0, tokenReal.length() - 1) + "X";

        // Un string que tiene la estructura de JWT pero no es real
        String tokenBasura = "eyJhbGciOiJIUzI1NiJ9.hacker_payload.firma_inventada_123";

        // Act
        boolean alteradoEsValido = jwtUtils.isTokenValid(tokenAlterado);
        boolean basuraEsValida = jwtUtils.isTokenValid(tokenBasura);

        // Assert
        assertFalse(alteradoEsValido, "Un token con un solo carácter alterado DEBE ser rechazado (Firma inválida)");
        assertFalse(basuraEsValida, "Un texto que imita ser un token DEBE ser rechazado");
    }
}