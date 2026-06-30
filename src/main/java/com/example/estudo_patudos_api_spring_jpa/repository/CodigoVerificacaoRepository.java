package com.example.estudo_patudos_api_spring_jpa.repository;

import com.example.estudo_patudos_api_spring_jpa.model.CodigoVerificacao;
import com.example.estudo_patudos_api_spring_jpa.model.TipoCodigo;
import com.example.estudo_patudos_api_spring_jpa.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CodigoVerificacaoRepository extends JpaRepository<CodigoVerificacao, Long> {

    // Código ativo mais recente do usuário para um tipo (usado na validação e no cooldown).
    Optional<CodigoVerificacao> findTopByUsuarioAndTipoAndUsadoFalseOrderByCriadoEmDesc(Usuario usuario, TipoCodigo tipo);

    // Invalida códigos anteriores ao gerar um novo (só um código ativo por tipo).
    @Modifying
    @Query("update CodigoVerificacao c set c.usado = true where c.usuario = :usuario and c.tipo = :tipo and c.usado = false")
    void invalidarAtivos(Usuario usuario, TipoCodigo tipo);
}
