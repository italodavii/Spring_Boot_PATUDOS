package com.example.estudo_patudos_api_spring_jpa.dto.auth;

public record LoginRequest(
        String email,
        String senha
) {}
