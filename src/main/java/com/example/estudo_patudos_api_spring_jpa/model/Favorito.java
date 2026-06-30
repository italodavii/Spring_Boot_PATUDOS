package com.example.estudo_patudos_api_spring_jpa.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// Junção usuário <-> pet favoritado. Um usuário só pode favoritar o mesmo pet uma vez
// (unique em usuario_id + pet_id). O endpoint de toggle remove/adiciona conforme o estado.
@Entity
@Data
@Table(name = "favorito", uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "pet_id"}))
@EntityListeners(AuditingEntityListener.class)
public class Favorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Pet tem @SQLRestriction("ativo = true"): se o pet for excluído (soft delete),
    // esta associação volta nula no fetch — o service filtra esses casos.
    @ManyToOne(optional = false)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime criadoEm;
}
