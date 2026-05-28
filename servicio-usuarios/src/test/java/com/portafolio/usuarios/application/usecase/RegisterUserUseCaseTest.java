package com.portafolio.usuarios.application.usecase;

import com.portafolio.usuarios.application.dto.RegisterUserCommand;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    @Test
    @DisplayName("Debería registrar un usuario exitosamente")
    void shouldRegisterUser() {
        RegisterUserCommand command = new RegisterUserCommand("nuevo@test.com", "pass123", "USER");
        User savedUser = User.restore(1L, "nuevo@test.com", "pass123", "USER");

        when(userRepository.findByEmail(command.email())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = registerUserUseCase.execute(command);

        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debería lanzar error si el email ya existe")
    void shouldThrowErrorWhenEmailExists() {
        RegisterUserCommand command = new RegisterUserCommand("existe@test.com", "pass123", "USER");
        User existingUser = User.restore(1L, "existe@test.com", "pass123", "USER");

        when(userRepository.findByEmail(command.email())).thenReturn(Optional.of(existingUser));

        DomainException exception = assertThrows(DomainException.class, () ->
                registerUserUseCase.execute(command)
        );

        assertEquals(ErrorCode.USER_ALREADY_EXISTS, exception.getErrorCode());
        verify(userRepository, never()).save(any(User.class));
    }
}