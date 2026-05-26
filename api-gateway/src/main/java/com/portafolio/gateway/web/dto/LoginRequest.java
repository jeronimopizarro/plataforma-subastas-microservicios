package com.portafolio.gateway.web.dto;

public record LoginRequest(
        String username,
        String password
) {}