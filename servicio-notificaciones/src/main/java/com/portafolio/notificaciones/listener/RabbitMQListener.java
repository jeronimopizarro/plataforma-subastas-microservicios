package com.portafolio.notificaciones.listener;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.portafolio.notificaciones.client.AuctionClient;
import com.portafolio.notificaciones.client.UserClient;
import com.portafolio.notificaciones.client.dto.AuctionResponseDTO;
import com.portafolio.notificaciones.client.dto.ProductResponseDTO;
import com.portafolio.notificaciones.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


import java.util.Map;

@Component
public class RabbitMQListener {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;
    private final UserClient userClient;
    private final AuctionClient auctionClient;
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQListener.class);

    public RabbitMQListener(EmailService emailService,
                            ObjectMapper objectMapper,
                            UserClient userClient,
                            AuctionClient auctionClient) {
        this.emailService = emailService;
        this.objectMapper = objectMapper;
        this.userClient = userClient;
        this.auctionClient = auctionClient;
    }

    @RabbitListener(queues = "subastas.notificaciones.email")
    public void receiveMessage(String jsonMessage) {
        logger.info("📬 Evento Ligero recibido desde RabbitMQ: {}", jsonMessage);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = objectMapper.readValue(jsonMessage, Map.class);

            Long outbidUserId = ((Number) payload.get("outbidUserId")).longValue();
            Long auctionId = ((Number) payload.get("auctionId")).longValue();

            logger.info("🔍 Consultando datos para usuario {} y subasta {}...", outbidUserId, auctionId);

            String email = userClient.getUserEmail(outbidUserId).get("email");
            AuctionResponseDTO auction = auctionClient.getAuctionById(auctionId);
            ProductResponseDTO product = auctionClient.getProductById(auction.productId());

            String asunto = "¡Atención! Han superado tu puja";
            String mensaje = String.format("Usuario: tu puja en el producto \"%s\" ha sido superada.", product.title());

            emailService.sendEmail(email, asunto, mensaje);

        } catch (Exception e) {
            logger.error("❌ Error al procesar el enriquecimiento de la notificación: {}", e.getMessage());
        }
    }
}