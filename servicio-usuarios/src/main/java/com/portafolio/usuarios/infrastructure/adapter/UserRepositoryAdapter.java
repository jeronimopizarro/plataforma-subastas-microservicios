package com.portafolio.usuarios.infrastructure.adapter;

import com.portafolio.usuarios.domain.entity.User;
import com.portafolio.usuarios.domain.repository.UserRepository;
import com.portafolio.usuarios.infrastructure.entity.UserEntity;
import com.portafolio.usuarios.infrastructure.repository.JpaUserRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    public UserRepositoryAdapter(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
                .map(this::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaUserRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = UserEntity.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .build();

        return toDomain(jpaUserRepository.save(entity));
    }

    private User toDomain(UserEntity entity) {
        return User.restore(
                entity.getId(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRole()
        );
    }
}