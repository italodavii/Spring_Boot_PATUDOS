package com.example.estudo_patudos_api_spring_jpa.service;

import com.example.estudo_patudos_api_spring_jpa.model.Pet;
import com.example.estudo_patudos_api_spring_jpa.repository.PetRepository;
import com.example.estudo_patudos_api_spring_jpa.specifications.PetSpecifications;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    public Page<Pet> listarTodos(String especie, String genero, String idade, String status, boolean incluirInativos, Pageable pageable) {
        Specification<Pet> spec = (root, query, cb) -> {
            if (!incluirInativos) {
                return cb.equal(root.get("ativo"), true);
            }
            return cb.conjunction();
        };

        if (especie != null && !especie.isBlank() && !especie.equalsIgnoreCase("todos")) {
            spec = spec.and(PetSpecifications.comEspecie(especie.toLowerCase().trim()));
        }

        if (genero != null && !genero.isBlank() && !genero.equalsIgnoreCase("todos")) {
            spec = spec.and(PetSpecifications.comGenero(genero.toLowerCase().trim()));
        }

        if (idade != null && !idade.isBlank() && !idade.equalsIgnoreCase("todos")) {
            spec = spec.and(PetSpecifications.comIdade(idade.toLowerCase().trim()));
        }

        if (status != null && !status.isBlank()) {
            spec = spec.and(PetSpecifications.comStatus(status.toLowerCase().trim()));
        }

        return petRepository.findAll(spec, pageable);
    }

    public Pet salvar(Pet pet) {
        pet.setEspecie(pet.getEspecie().toLowerCase().trim());
        pet.setStatus(pet.getStatus().toLowerCase().trim());
        pet.setNome(pet.getNome().trim());
        pet.setGenero(pet.getGenero().toLowerCase().trim());
        pet.setIdade(pet.getIdade().toLowerCase().trim());

        // Garante que todo novo pet comece ativo
        if (pet.getId() == null) {
            pet.setAtivo(true);
        }

        return petRepository.save(pet);
    }

    // Atualiza apenas campos editáveis. ativo e status NÃO são tocados aqui —
    // transições de status passam por marcarComoAdotado() / excluir().
    @Transactional
    public Pet atualizar(Long id, Pet dados) {
        Pet existente = petRepository.findById(id).orElseThrow();

        if (dados.getNome() != null) existente.setNome(dados.getNome().trim());
        if (dados.getEspecie() != null) existente.setEspecie(dados.getEspecie().toLowerCase().trim());
        if (dados.getIdade() != null) existente.setIdade(dados.getIdade().toLowerCase().trim());
        if (dados.getGenero() != null) existente.setGenero(dados.getGenero().toLowerCase().trim());
        if (dados.getDescricao() != null) existente.setDescricao(dados.getDescricao());
        if (dados.getUrlFoto() != null && !dados.getUrlFoto().isBlank()) {
            existente.setUrlFoto(dados.getUrlFoto());
        }

        return petRepository.save(existente);
    }

    @Transactional
    public void marcarComoAdotado(Long id) {
        Pet pet = petRepository.findById(id).orElseThrow();
        pet.setAtivo(true);
        pet.setStatus("adotado");
        petRepository.save(pet);
    }

    @Transactional
    public void excluir(Long id) {
        Pet pet = petRepository.findById(id).orElseThrow();
        pet.setAtivo(false); // Soft delete
        pet.setStatus("excluido");
        petRepository.save(pet);
    }
}