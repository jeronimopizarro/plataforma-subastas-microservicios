package com.portafolio.usuarios.web.controller;

import com.portafolio.usuarios.application.usecase.FindUserByIdUseCase;
import com.portafolio.usuarios.domain.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final FindUserByIdUseCase findUserByIdUseCase;

    public UserController(FindUserByIdUseCase findUserByIdUseCase) {
        this.findUserByIdUseCase = findUserByIdUseCase;
    }

    @GetMapping("/{id}/email")
    public ResponseEntity<Map<String, String>> getUserEmail(@PathVariable Long id) {
        User user = findUserByIdUseCase.execute(id);

        return ResponseEntity.ok(Map.of("email", user.getEmail()));
    }
}