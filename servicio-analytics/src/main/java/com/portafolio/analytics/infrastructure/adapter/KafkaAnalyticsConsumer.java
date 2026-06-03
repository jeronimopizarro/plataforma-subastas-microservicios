package com.portafolio.analytics.infrastructure.adapter;

import com.portafolio.analytics.domain.model.BidAnalytics;
import com.portafolio.analytics.infrastructure.event.BidAnalyticsEvent;
import com.portafolio.analytics.infrastructure.repository.BidAnalyticsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaAnalyticsConsumer {

    private final BidAnalyticsRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(KafkaAnalyticsConsumer.class);

    public KafkaAnalyticsConsumer(BidAnalyticsRepository repository) {
        this.repository = repository;
    }

    // Le indica a Spring que ejecute este método cada vez que llega un mensaje
    @KafkaListener(topics = "${topic.analytics.bidding}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBidEvent(BidAnalyticsEvent event) {
        logger.info("📥 Evento capturado desde Kafka: {}", event);

        try {
            BidAnalytics entity = BidAnalytics.builder()
                    .auctionId(event.auctionId())
                    .bidderId(event.bidderId())
                    .amount(event.amount())
                    .timestamp(event.timestamp())
                    .eventType(event.eventType())
                    .build();

            repository.save(entity);
            logger.info("✅ Dato insertado en PostgreSQL (Analytics DB) exitosamente.");

        } catch (Exception e) {
            logger.error("❌ Error al guardar en PostgreSQL: {}", e.getMessage());
        }
    }
}