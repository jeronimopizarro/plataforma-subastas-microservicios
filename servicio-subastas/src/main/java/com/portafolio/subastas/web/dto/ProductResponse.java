package com.portafolio.subastas.web.dto;

public record ProductResponse(
        Long id,
        String title,
        String description,
        String condition,
        String imageUrl,
        Long sellerId,
        boolean active
) {
}
