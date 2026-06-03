package com.portafolio.bidding.infrastructure.adapter;

import com.portafolio.bidding.infrastructure.event.BidAnalyticsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class KafkaAnalyticsPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(KafkaAnalyticsPublisher.class);

    @Value("${topic.analytics.bidding}")
    private String topicName;

    public KafkaAnalyticsPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishBidEvent(Long auctionId, Long bidderId, BigDecimal amount) {
        try {
            BidAnalyticsEvent event = new BidAnalyticsEvent(
                    auctionId,
                    bidderId,
                    amount,
                    LocalDateTime.now(),
                    "BID_PLACED"
            );

            // Usamos el ID de la subasta como clave
            kafkaTemplate.send(topicName, auctionId.toString(), event);
            logger.info("📊 Evento Analítico enviado a Kafka: {}", event);

        } catch (Exception e) {
            logger.error("❌ Error al enviar evento analítico a Kafka", e);
        }
    }
}