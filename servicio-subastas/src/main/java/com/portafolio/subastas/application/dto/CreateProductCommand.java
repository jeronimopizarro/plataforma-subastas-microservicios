package com.portafolio.subastas.application.dto;

public record CreateProductCommand(
        String title,
        String description,
        String condition,
        String imageUrl,
        Long sellerId
) {
}
