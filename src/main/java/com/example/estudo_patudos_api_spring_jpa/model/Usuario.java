package com.example.estudo_patudos_api_spring_jpa.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "usuario")
@EntityListeners(AuditingEntityListener.class)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeCompleto;

    @Column(nullable = false, unique = true)
    private String email;

    // Nullable: contas criadas via Google podem não ter telefone (preenchido no perfil depois).
    private String telefone;

    // Hash BCrypt — nunca exposto nas respostas (ver UsuarioDTO).
    // Nullable: contas só-Google não têm senha (login apenas social).
    private String senhaHash;

    // Marca social: o "sub" do Google quando a conta foi vinculada/criada via Google.
    @Column(unique = true)
    private String googleId;

    // E-mail confirmado por código? Contas Google entram já verificadas; cadastro local começa false.
    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean emailVerificado = false;

    // Troca protegida de e-mail (conta já verificada): guarda o novo e-mail até o código ser
    // confirmado. O e-mail atual continua valendo até a confirmação. Null = sem troca em andamento.
    private String emailPendente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false)
    @ColumnDefault("true")
    private boolean receberEmails = true;

    // Endereço — NÃO coletado no cadastro. Preenchido na intenção de adoção (Fase 3)
    // e editável no perfil. Todos nullable.
    private String cidade;
    private String estado;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cep;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime criadoEm;
}
