package com.example.estudo_patudos_api_spring_jpa.dto.auth;

import com.example.estudo_patudos_api_spring_jpa.model.Usuario;

import java.time.LocalDateTime;

// Representacao segura do usuario (NUNCA expoe senhaHash).
public record UsuarioDTO(
        Long id,
        String nomeCompleto,
        String email,
        String telefone,
        String role,
        boolean receberEmails,
        boolean emailVerificado,
        String emailPendente,
        boolean temSenha,
        String cidade,
        String estado,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cep,
        LocalDateTime criadoEm
) {
    public static UsuarioDTO de(Usuario u) {
        return new UsuarioDTO(
                u.getId(), u.getNomeCompleto(), u.getEmail(), u.getTelefone(),
                u.getRole().name(), u.isReceberEmails(), u.isEmailVerificado(),
                u.getEmailPendente(), u.getSenhaHash() != null,
                u.getCidade(), u.getEstado(), u.getLogradouro(), u.getNumero(),
                u.getComplemento(), u.getBairro(), u.getCep(),
                u.getCriadoEm()
        );
    }
}
