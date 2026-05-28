package com.portafolio.usuarios.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("Debería crear un nuevo usuario válido")
    void shouldCreateNewUser() {
        User user = User.createNew("test@test.com", "pass123", "USER");

        assertNull(user.getId());
        assertEquals("test@test.com", user.getEmail());
        assertEquals("pass123", user.getPassword());
        assertEquals("USER", user.getRole());
    }

    @Test
    @DisplayName("Debería fallar al crear un usuario sin email")
    void shouldFailWhenEmailIsBlank() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                User.createNew("", "pass123", "USER")
        );
        assertEquals("El correo electrónico es obligatorio", exception.getMessage());
    }

    @Test
    @DisplayName("Debería restaurar un usuario existente")
    void shouldRestoreUser() {
        User user = User.restore(1L, "test@test.com", "pass123", "USER");

        assertEquals(1L, user.getId());
        assertEquals("test@test.com", user.getEmail());
    }
}