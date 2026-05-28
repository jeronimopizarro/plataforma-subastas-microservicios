package com.portafolio.usuarios.application.usecase;

import com.portafolio.usuarios.application.dto.RegisterUserCommand;
import com.portafolio.usuarios.domain.entity.User;
import com.portafolio.usuarios.domain.exception.DomainException;
import com.portafolio.usuarios.domain.exception.ErrorCode;
import com.portafolio.usuarios.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class RegisterUserUseCase {

    private final UserRepository userRepository;

    public RegisterUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(RegisterUserCommand command) {
        if (userRepository.findByEmail(command.email()).isPresent()) {
            throw new DomainException(ErrorCode.USER_ALREADY_EXISTS);
        }

        User newUser = User.createNew(command.email(), command.password(), command.role());
        return userRepository.save(newUser);
    }
}