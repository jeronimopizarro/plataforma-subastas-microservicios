package com.portafolio.wallet.infrastructure.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CorrelationIdFilter implements Filter {

    // Agregamos un logger propio para el filtro
    private static final Logger logger = LoggerFactory.getLogger(CorrelationIdFilter.class);

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String CORRELATION_ID_LOG_VAR = "correlationId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response; // Casteamos la respuesta

        String correlationId = req.getHeader(CORRELATION_ID_HEADER);

        if (correlationId != null) {
            MDC.put(CORRELATION_ID_LOG_VAR, correlationId);
        }

        logger.info("➡ Petición IN: {} {}", req.getMethod(), req.getRequestURI());

        long startTime = System.currentTimeMillis(); // Cronometramos cuánto tarda

        try {
            chain.doFilter(request, response);
        } finally {
            // --- AUTOMATIZACIÓN 2: Log de Salida ---
            long duration = System.currentTimeMillis() - startTime;
            logger.info("⬅ Petición OUT: {} {} - Estado: {} - Tiempo: {}ms",
                    req.getMethod(), req.getRequestURI(), res.getStatus(), duration);

            MDC.remove(CORRELATION_ID_LOG_VAR);
        }
    }
}