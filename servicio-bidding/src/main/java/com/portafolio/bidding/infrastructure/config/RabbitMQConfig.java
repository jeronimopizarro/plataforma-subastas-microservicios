package com.portafolio.bidding.infrastructure.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Definimos el nombre exacto de la cola donde dejaremos los mensajes
    public static final String NOTIFICATIONS_QUEUE = "subastas.notificaciones.email";

    @Bean
    public Queue notificationsQueue() {
        // true = La cola sobrevive si RabbitMQ se reinicia (durable)
        return new Queue(NOTIFICATIONS_QUEUE, true);
    }
}