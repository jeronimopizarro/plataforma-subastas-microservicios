package com.portafolio.bidding.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilita un broker en memoria para enviar mensajes a los clientes en rutas que empiecen con /topic
        config.enableSimpleBroker("/topic");
        // Prefijo para los mensajes que envíen los clientes al servidor (si hiciera falta)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Este es el endpoint al que se conectará el Frontend (React/Angular) para abrir el túnel
        registry.addEndpoint("/ws-bidding")
                .setAllowedOriginPatterns("*") // Permite conexiones desde cualquier origen (CORS)
                .withSockJS(); // Soporte de retrocompatibilidad si el navegador falla con WebSockets puros
    }
}