package com.example.estudo_patudos_api_spring_jpa.repository;

import com.example.estudo_patudos_api_spring_jpa.model.IntencaoAdocao;
import com.example.estudo_patudos_api_spring_jpa.model.Pet;
import com.example.estudo_patudos_api_spring_jpa.model.StatusIntencao;
import com.example.estudo_patudos_api_spring_jpa.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface IntencaoAdocaoRepository extends JpaRepository<IntencaoAdocao, Long> {

    // Lista para o admin: mais recentes primeiro. Snapshots tornam a leitura single-table
    // (sem tocar nas associações usuario/pet), evitando N+1 e o @SQLRestriction da Pet.
    List<IntencaoAdocao> findAllByOrderByCriadoEmDesc();

    // Intenções de um usuário específico (seção "Minhas Intenções" do perfil).
    List<IntencaoAdocao> findByUsuarioOrderByCriadoEmDesc(Usuario usuario);

    // Evita intenções duplicadas: já existe uma do mesmo usuário p/ o mesmo pet num dos status?
    boolean existsByUsuarioAndPetAndStatusIn(Usuario usuario, Pet pet, Collection<StatusIntencao> status);

    long countByStatus(StatusIntencao status);
}
