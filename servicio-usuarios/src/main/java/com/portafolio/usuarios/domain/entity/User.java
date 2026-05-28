package com.portafolio.usuarios.domain.entity;

import lombok.Getter;

@Getter
public class User {

    private final Long id;
    private final String email;
    private final String password;
    private final String role;

    private User(Long id, String email, String password, String role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static User createNew(String email, String password, String role) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        return new User(null, email, password, role);
    }

    public static User restore(Long id, String email, String password, String role) {
        return new User(id, email, password, role);
    }
}