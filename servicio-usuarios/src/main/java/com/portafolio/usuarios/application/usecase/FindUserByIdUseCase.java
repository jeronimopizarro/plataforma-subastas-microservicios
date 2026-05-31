package com.portafolio.usuarios.application.usecase;

import com.portafolio.usuarios.domain.entity.User;
import com.portafolio.usuarios.domain.exception.UserNotFoundException;
import com.portafolio.usuarios.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class FindUserByIdUseCase {

    private final UserRepository userRepository;

    public FindUserByIdUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario con ID " + id + " no encontrado"));
    }
}