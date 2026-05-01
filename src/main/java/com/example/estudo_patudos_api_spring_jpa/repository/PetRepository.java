package com.example.estudo_patudos_api_spring_jpa.repository;

import com.example.estudo_patudos_api_spring_jpa.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long>, JpaSpecificationExecutor<Pet> {

    // Para o dashboard: filtra por quem NÃO foi excluído e pelo status
    long countByAtivoTrueAndStatus(String status);

    // Conta todos os que estão no sistema (independente de status), mas ignora excluídos
    long countByAtivoTrue();

    // Conta todos os dados do banco inclusive os inativos
    long countByStatus(String status);
}


