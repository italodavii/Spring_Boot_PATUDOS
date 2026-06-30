package com.example.estudo_patudos_api_spring_jpa.dto.auth;

// Atualizacao do proprio perfil (PATCH /api/auth/me). Campos null = nao alterar.
// Email, senha e role NAO sao editaveis por aqui.
public record UpdateMeRequest(
        String nomeCompleto,
        String telefone,
        Boolean receberEmails,
        String cidade,
        String estado,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cep
) {}
