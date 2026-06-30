package com.example.estudo_patudos_api_spring_jpa.dto.auth;

// Código de 6 dígitos que o usuário recebeu por e-mail.
public record VerificarEmailRequest(String codigo) {}
