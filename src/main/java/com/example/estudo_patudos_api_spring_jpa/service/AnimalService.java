package com.example.estudo_patudos_api_spring_jpa.service;

import com.example.estudo_patudos_api_spring_jpa.dto.AnimalDTO;
import com.example.estudo_patudos_api_spring_jpa.model.Pet;
import com.example.estudo_patudos_api_spring_jpa.repository.AnimalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnimalService {

    private final AnimalRepository repository;

    public AnimalService(AnimalRepository repository) {
        this.repository = repository;
    }

    public AnimalDTO converterParaDTO(Pet pet) {
        return new AnimalDTO(
                pet.getId(),
                pet.getNome(),
                pet.getEspecie(),
                pet.getInstituicao() != null ? pet.getInstituicao().getNome() : "Sem Instituição"
        );
    }

    public List<AnimalDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    public Pet salvar(Pet pet) {
        // Exemplo de regra de negócio:
        if (pet.getNome() == null || pet.getNome().isEmpty()) {
            throw new RuntimeException("Nome do animal é obrigatório!");
        }
        // 2. Verificação de Relacionamento
        if (pet.getInstituicao() != null && pet.getInstituicao().getId() != null) {
            // Opcional: Você poderia buscar no banco para validar se a Instituição existe
            // Mas o JPA já tentará fazer esse vínculo pelo ID enviado.
        }
        return repository.save(pet);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}
