package com.portafolio.notificaciones.listener;

import com.portafolio.notificaciones.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQListener {

    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQListener.class);

    public RabbitMQListener(EmailService emailService) {
        this.emailService = emailService;
    }

    // Conecta con la cola específica de RabbitMQ
    @RabbitListener(queues = "subastas.notificaciones.email")
    public void receiveMessage(String mensaje) {
        logger.info("📬 Mensaje recibido desde la cola: {}", mensaje);

        // vamos a generar un destinatario dinámico de prueba usando Mailtrap.
        String destinatario = "usuario-superado@sistema.com";
        String asunto = "¡Atención! Han superado tu puja";

        // Enviamos el correo usando nuestro servicio
        emailService.sendEmail(destinatario, asunto, mensaje);
    }
}