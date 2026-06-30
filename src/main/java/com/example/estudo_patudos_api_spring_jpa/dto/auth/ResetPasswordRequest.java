package com.example.estudo_patudos_api_spring_jpa.dto.auth;

// Redefinição de senha com o código recebido por e-mail.
public record ResetPasswordRequest(String email, String codigo, String novaSenha) {}
