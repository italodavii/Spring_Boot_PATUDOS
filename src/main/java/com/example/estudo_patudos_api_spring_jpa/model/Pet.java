package com.example.estudo_patudos_api_spring_jpa.model;

import jakarta.persistence.*;
import lombok.Data; // O Lombok gera Getters/Setters sozinho!
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE PET SET ativo = false, status = 'excluido' WHERE id = ?") // Soft delete: desativa e marca como excluído
@SQLRestriction("ativo = true") // Substitui o @Where
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String especie;

    @Column(nullable = false)
    private String idade;

    @Column(nullable = false)
    private String urlFoto;

    @Column(nullable = false)
    private String genero;

    @Column(nullable = false)
    private String descricao;

    // Regra: Sempre inicia como "disponivel"
    @Column(nullable = false)
    private String status = "disponivel";

    @Column(nullable = false)
    private boolean ativo = true;

    // Cuidados veterinários — o admin marca no cadastro; exibidos no popup do pet.
    // @ColumnDefault garante DEFAULT false no ALTER TABLE (ddl-auto=update) para
    // não quebrar com as linhas já existentes.
    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean vacinado = false;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean castrado = false;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean vermifugado = false;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime criadoEm;
}

