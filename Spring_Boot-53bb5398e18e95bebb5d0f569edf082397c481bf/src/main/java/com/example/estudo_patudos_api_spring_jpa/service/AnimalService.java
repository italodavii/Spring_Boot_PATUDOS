package com.example.estudo_patudos_api_spring_jpa.service;

import com.example.estudo_patudos_api_spring_jpa.dto.AnimalDTO;
import com.example.estudo_patudos_api_spring_jpa.model.Animal;
import com.example.estudo_patudos_api_spring_jpa.repository.AnimalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnimalService {

    private final AnimalRepository repository;

    public AnimalService(AnimalRepository repository) {
        this.repository = repository;
    }

    public AnimalDTO converterParaDTO(Animal animal) {
        return new AnimalDTO(
                animal.getId(),
                animal.getNome(),
                animal.getEspecie(),
                animal.getInstituicao() != null ? animal.getInstituicao().getNome() : "Sem Instituição"
        );
    }

    public List<AnimalDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    public Animal salvar(Animal animal) {
        // Exemplo de regra de negócio:
        if (animal.getNome() == null || animal.getNome().isEmpty()) {
            throw new RuntimeException("Nome do animal é obrigatório!");
        }
        // 2. Verificação de Relacionamento
        if (animal.getInstituicao() != null && animal.getInstituicao().getId() != null) {
            // Opcional: Você poderia buscar no banco para validar se a Instituição existe
            // Mas o JPA já tentará fazer esse vínculo pelo ID enviado.
        }
        return repository.save(animal);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}
