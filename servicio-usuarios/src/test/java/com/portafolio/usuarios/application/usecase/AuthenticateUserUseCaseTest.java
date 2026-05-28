package com.portafolio.usuarios.application.usecase;

import com.portafolio.usuarios.application.dto.AuthenticateUserCommand;
import com.portafolio.usuarios.domain.entity.User;
import com.portafolio.usuarios.domain.exception.DomainException;
import com.portafolio.usuarios.domain.exception.ErrorCode;
import com.portafolio.usuarios.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticateUserUseCase authenticateUserUseCase;

    @Test
    @DisplayName("Debería autenticar al usuario correctamente")
    void shouldAuthenticateUser() {
        AuthenticateUserCommand command = new AuthenticateUserCommand("test@test.com", "pass123");
        User mockUser = User.restore(1L, "test@test.com", "pass123", "USER");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));

        User result = authenticateUserUseCase.execute(command);

        assertEquals(1L, result.getId());
        assertEquals("test@test.com", result.getEmail());
    }

    @Test
    @DisplayName("Debería lanzar error por credenciales inválidas")
    void shouldThrowErrorForInvalidCredentials() {
        AuthenticateUserCommand command = new AuthenticateUserCommand("test@test.com", "wrongpass");
        User mockUser = User.restore(1L, "test@test.com", "pass123", "USER");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));

        DomainException exception = assertThrows(DomainException.class, () ->
                authenticateUserUseCase.execute(command)
        );

        assertEquals(ErrorCode.INVALID_CREDENTIALS, exception.getErrorCode());
    }
}