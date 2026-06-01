package com.portafolio.bidding.infrastructure.adapter;

import com.portafolio.bidding.infrastructure.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQNotificationPublisher {

    private final RabbitTemplate rabbitTemplate;
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQNotificationPublisher.class);

    public RabbitMQNotificationPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendOutbidNotification(Long previousBidderId, Long auctionId) {
        String mensaje = String.format("Usuario %d: Tu puja en la subasta %d ha sido superada.", previousBidderId, auctionId);

        // Se suma a la cola
        rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATIONS_QUEUE, mensaje);

        logger.info("📬 Evento enviado a RabbitMQ: {}", mensaje);
    }
}