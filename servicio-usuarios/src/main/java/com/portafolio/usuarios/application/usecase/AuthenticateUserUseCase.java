package com.portafolio.usuarios.application.usecase;

import com.portafolio.usuarios.application.dto.AuthenticateUserCommand;
import com.portafolio.usuarios.domain.entity.User;
import com.portafolio.usuarios.domain.exception.DomainException;
import com.portafolio.usuarios.domain.exception.ErrorCode;
import com.portafolio.usuarios.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateUserUseCase {

    private final UserRepository userRepository;

    public AuthenticateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(AuthenticateUserCommand command) {
        return userRepository.findByEmail(command.email())
                .filter(user -> user.getPassword().equals(command.password()))
                .orElseThrow(() -> new DomainException(ErrorCode.INVALID_CREDENTIALS));
    }
}