package com.portafolio.gateway.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

// Levantamos el Gateway en un puerto aleatorio para hacerle peticiones HTTP reales
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class RateLimiterIntegrationTest {

    // Testcontainers que descargue y levante un Redis
    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    // Redis se levanta en un puerto dinámico para no chocar con nada.
    // Le inyectamos a Spring Boot ese puerto exacto para que sepa dónde conectarse.
    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
    }

    @Autowired
    private WebTestClient webClient;

    @Test
    @DisplayName("Debe bloquear la petición si excede la capacidad de ráfaga (Burst Capacity)")
    void rateLimiterBloqueaExcesoDePeticiones() {

        // Disparamos 20 peticiones seguidas al endpoint de login.
        int totalPeticiones = 20;
        int peticionesBloqueadas = 0;

        for (int i = 0; i < totalPeticiones; i++) {
            webClient.post().uri("/auth/login")
                    .exchange()
                    .expectBody()
                    .consumeWith(response -> {

                        if (response.getStatus() == HttpStatus.TOO_MANY_REQUESTS) {
                            System.out.println("¡Petición " + response.getStatus() + " bloqueada con éxito!");
                            throw new RateLimitExceededException(); // Excepción de control interno del test
                        }
                    });
        }
    }

    // Clase auxiliar para salir del bucle en cuanto detectemos el bloqueo
    private static class RateLimitExceededException extends RuntimeException {}
}