package com.example.estudo_patudos_api_spring_jpa.dto.auth;

// Pedido de recuperação de senha — só o e-mail.
public record ForgotPasswordRequest(String email) {}
