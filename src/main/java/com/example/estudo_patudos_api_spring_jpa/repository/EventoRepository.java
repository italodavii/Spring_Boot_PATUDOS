package com.example.estudo_patudos_api_spring_jpa.repository;

import com.example.estudo_patudos_api_spring_jpa.model.Evento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    // Catálogo público / Home — apenas eventos agendados (data >= hoje), em ordem cronológica.
    // O filtro ativo=true já é aplicado pelo @SQLRestriction da entidade.
    List<Evento> findByDataGreaterThanEqualOrderByDataAsc(LocalDate data);

    Page<Evento> findByDataGreaterThanEqualOrderByDataAsc(LocalDate data, Pageable pageable);

    long countByDataBefore(LocalDate data);

    long countByDataAfter(LocalDate hoje);
}
