package com.example.estudo_patudos_api_spring_jpa.dto.auth;

// Novo e-mail informado pelo usuário no perfil (caso tenha errado no cadastro).
public record TrocarEmailRequest(String novoEmail) {}
