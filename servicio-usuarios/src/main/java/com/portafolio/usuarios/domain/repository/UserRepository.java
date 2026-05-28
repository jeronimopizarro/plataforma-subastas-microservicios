package com.portafolio.usuarios.domain.repository;

import com.portafolio.usuarios.domain.entity.User;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    User save(User user);
}