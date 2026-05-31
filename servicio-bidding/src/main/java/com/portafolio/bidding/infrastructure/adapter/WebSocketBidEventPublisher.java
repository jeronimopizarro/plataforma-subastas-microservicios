package com.portafolio.bidding.infrastructure.adapter;

import com.portafolio.bidding.application.port.BidEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class WebSocketBidEventPublisher implements BidEventPublisher {

    // Herramienta de Spring para enviar mensajes al broker
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketBidEventPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void publishNewBid(Long auctionId, Long bidderId, String bidderEmail, BigDecimal amount) {
        // Armamos el "paquete" de datos que le llegará al Frontend
        Map<String, Object> notification = Map.of(
                "auctionId", auctionId,
                "newWinnerId", bidderId,
                "bidderEmail", bidderEmail,
                "amount", amount,
                "timestamp", LocalDateTime.now()
        );

        // Emitimos el mensaje a una "sala" específica de esa subasta (ej: /topic/auctions/1)
        messagingTemplate.convertAndSend("/topic/auctions/" + auctionId, notification);
    }
}