package com.portafolio.usuarios.infrastructure.config;

import com.portafolio.usuarios.domain.entity.User;
import com.portafolio.usuarios.domain.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("admin@subastas.com").isEmpty()) {
            User defaultUser = User.createNew("admin@subastas.com", "admin123", "USER");
            userRepository.save(defaultUser);
            System.out.println("⚡ Usuario de prueba inicial ('admin@subastas.com') creado con éxito.");
        }
    }
}