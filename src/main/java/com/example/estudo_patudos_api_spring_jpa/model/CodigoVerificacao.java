package com.example.estudo_patudos_api_spring_jpa.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Código de verificação (OTP) enviado por e-mail. Mesmo mecanismo para confirmar o e-mail
 * no cadastro e para recuperar a senha. O código de 6 dígitos NÃO é salvo em texto puro —
 * guarda-se apenas o hash BCrypt. Expira em alguns minutos e tem limite de tentativas.
 */
@Entity
@Data
@Table(name = "codigo_verificacao")
@EntityListeners(AuditingEntityListener.class)
public class CodigoVerificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCodigo tipo;

    // Hash BCrypt do código de 6 dígitos.
    @Column(nullable = false)
    private String codigoHash;

    @Column(nullable = false)
    private LocalDateTime expiraEm;

    @Column(nullable = false)
    private boolean usado = false;

    // Tentativas de validação já feitas (anti-brute-force).
    @Column(nullable = false)
    private int tentativas = 0;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime criadoEm;
}
