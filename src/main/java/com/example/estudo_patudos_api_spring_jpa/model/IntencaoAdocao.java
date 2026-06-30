package com.example.estudo_patudos_api_spring_jpa.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Solicitação de intenção de adoção feita por um adotante (USER) para um pet.
 *
 * Decisão de design (lição da Fase 2): os dados do adotante, do pet e do endereço são
 * SNAPSHOTADOS no envio. Assim a Ficha do Adotante no admin nunca precisa navegar a
 * associação Pet — que tem @SQLRestriction("ativo = true") e lançaria exceção se o pet
 * fosse excluído depois — e o registro fica historicamente fiel ao que foi enviado.
 * As FKs usuario/pet ficam como LAZY apenas para integridade/auditoria.
 */
@Entity
@Data
@Table(name = "intencao_adocao")
@EntityListeners(AuditingEntityListener.class)
public class IntencaoAdocao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FKs para integridade/auditoria (não usadas para montar a ficha — ver snapshots abaixo).
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    // --- Snapshot do adotante (no momento do envio) ---
    @Column(nullable = false)
    private String adotanteNome;
    @Column(nullable = false)
    private String adotanteEmail;
    @Column(nullable = false)
    private String adotanteTelefone;

    // --- Snapshot do pet ---
    @Column(nullable = false)
    private String petNome;
    private String petUrlFoto;

    // --- Endereço para a visita domiciliar (snapshot; também salvo de volta no perfil) ---
    @Column(nullable = false)
    private String logradouro;
    @Column(nullable = false)
    private String numero;
    private String complemento; // opcional
    @Column(nullable = false)
    private String bairro;
    @Column(nullable = false)
    private String cidade;
    @Column(nullable = false)
    private String estado;
    @Column(nullable = false)
    private String cep;

    // --- Termo + mensagem opcional ---
    @Column(nullable = false)
    private boolean termoAceito;

    @Column(columnDefinition = "TEXT")
    private String mensagem;

    // --- Entrevista de triagem (códigos estáveis; labels e alertas ficam no front) ---
    @Column(nullable = false)
    private String tipoResidencia;          // CASA | APARTAMENTO
    @Column(nullable = false)
    private String imovelSeguro;            // SEGURO | NAO_SEGURO
    @Column(nullable = false)
    private String tipoPosse;               // PROPRIO | ALUGADO
    private String alugadoAutorizaPets;     // SIM | NAO (só quando ALUGADO)
    @Column(nullable = false)
    private String tempoSozinho;            // MENOS_4H | ENTRE_4_8H | MAIS_8H
    @Column(nullable = false)
    private String outrosAnimais;           // NENHUM | CACHORRO | GATO | OUTROS
    private String animaisVacinadosCastrados; // TODOS | ALGUNS | NENHUM (só quando tem animais)
    @Column(nullable = false)
    private String familiaDeAcordo;         // TODOS_APROVAM | DIVERGENCIAS

    @Column(nullable = false, columnDefinition = "TEXT")
    private String planoViagemMudanca;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String planoComportamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("'PENDENTE'")
    private StatusIntencao status = StatusIntencao.PENDENTE;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime criadoEm;
}
