package com.portafolio.subastas.domain.entity;

import com.portafolio.subastas.domain.exception.InvalidProductException;
import lombok.Getter;

@Getter
public class Product {

    private final Long id;
    private String title;
    private String description;
    private String condition; // Ej: "NUEVO", "USADO"
    private String imageUrl;
    private final Long sellerId; // Admin que lo publica
    private boolean active;

    private Product(Long id, String title, String description, String condition,
                    String imageUrl, Long sellerId, boolean active) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.condition = condition;
        this.imageUrl = imageUrl;
        this.sellerId = sellerId;
        this.active = active;
        validate();
    }

    private void validate() {
        if (this.title == null || this.title.isBlank()) {
            throw new InvalidProductException("El título del producto es obligatorio.");
        }
        if (this.condition == null || this.condition.isBlank()) {
            throw new InvalidProductException("La condición del producto es obligatoria.");
        }
        if (this.sellerId == null) {
            throw new InvalidProductException("El producto debe estar asociado a un vendedor.");
        }
    }

    public static Product createNew(String title, String description, String condition,
                                    String imageUrl, Long sellerId) {
        return new Product(null, title, description, condition, imageUrl, sellerId, true);
    }

    public static Product restore(Long id, String title, String description, String condition,
                                  String imageUrl, Long sellerId, boolean active) {
        return new Product(id, title, description, condition, imageUrl, sellerId, active);
    }

    public void updateDetails(String title, String description, String condition, String imageUrl) {
        if (title != null) this.title = title;
        if (description != null) this.description = description;
        if (condition != null) this.condition = condition;
        if (imageUrl != null) this.imageUrl = imageUrl;
        validate();
    }

    public void deactivate() {
        if (!this.active) throw new InvalidProductException("El producto ya se encuentra inactivo.");
        this.active = false;
    }

    public void activate() {
        if (this.active) throw new InvalidProductException("El producto ya se encuentra activo.");
        this.active = true;
    }
}
