package com.example.estudo_patudos_api_spring_jpa.repository;

import com.example.estudo_patudos_api_spring_jpa.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByDataAfterOrderByDataAsc(LocalDate data);

    long countByDataBefore(LocalDate data);

    long countByDataAfter(LocalDate hoje);
}
