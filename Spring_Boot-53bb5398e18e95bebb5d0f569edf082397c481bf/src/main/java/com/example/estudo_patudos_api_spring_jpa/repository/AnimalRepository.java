package com.example.estudo_patudos_api_spring_jpa.repository;

import com.example.estudo_patudos_api_spring_jpa.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;

// Ao estender JpaRepository, o Spring já cria o código de SALVAR, DELETAR e BUSCAR
public interface AnimalRepository extends JpaRepository<Animal, Long> {
    // Não precisa escrever nada aqui dentro!
    // O JpaRepository já "te dá" o Save, Delete e Find sozinho.
}
