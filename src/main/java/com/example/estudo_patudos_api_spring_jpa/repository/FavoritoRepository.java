package com.example.estudo_patudos_api_spring_jpa.repository;

import com.example.estudo_patudos_api_spring_jpa.model.Favorito;
import com.example.estudo_patudos_api_spring_jpa.model.Pet;
import com.example.estudo_patudos_api_spring_jpa.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

    // Projeta só os ids dos pets favoritados (mais recentes primeiro). Usar f.pet.id usa a
    // coluna FK direto, SEM join na tabela PET — assim NÃO dispara o @SQLRestriction da Pet,
    // que num @ManyToOne eager lançaria exceção para pets soft-deletados. Os pets são carregados
    // depois via PetRepository (que aplica o @SQLRestriction e descarta os excluídos).
    @Query("select f.pet.id from Favorito f where f.usuario = :usuario order by f.criadoEm desc")
    List<Long> findPetIdsByUsuario(@Param("usuario") Usuario usuario);

    Optional<Favorito> findByUsuarioAndPet(Usuario usuario, Pet pet);
}
