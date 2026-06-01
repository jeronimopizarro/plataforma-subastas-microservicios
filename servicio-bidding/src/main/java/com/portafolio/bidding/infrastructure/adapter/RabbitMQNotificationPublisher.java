package com.portafolio.bidding.infrastructure.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portafolio.bidding.infrastructure.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RabbitMQNotificationPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQNotificationPublisher.class);

    public RabbitMQNotificationPublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendOutbidNotification(Long previousBidderId, Long auctionId) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("outbidUserId", previousBidderId);
            payload.put("auctionId", auctionId);

            String jsonMessage = objectMapper.writeValueAsString(payload);
            rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATIONS_QUEUE, jsonMessage);

            logger.info("📬 Evento Ligero (Thin Event) enviado a RabbitMQ: {}", jsonMessage);
        } catch (Exception e) {
            logger.error("Error al convertir el evento ligero a JSON", e);
        }
    }
}