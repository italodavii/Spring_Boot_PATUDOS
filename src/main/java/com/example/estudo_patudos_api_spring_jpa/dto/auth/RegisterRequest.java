package com.example.estudo_patudos_api_spring_jpa.dto.auth;

// Cadastro: só nome, email, telefone e senha (endereço não é coletado aqui).
public record RegisterRequest(
        String nomeCompleto,
        String email,
        String telefone,
        String senha
) {}
