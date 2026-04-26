package com.example.estudo_patudos_api_spring_jpa.controller;

import com.example.estudo_patudos_api_spring_jpa.dto.AnimalDTO;
import com.example.estudo_patudos_api_spring_jpa.model.Animal;
import com.example.estudo_patudos_api_spring_jpa.service.AnimalService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/animais")
public class AnimalController {

    private final AnimalService service; // O Controller agora só conhece o Service

    // Atualize o construtor para receber o Service
    public AnimalController(AnimalService service) {
        this.service = service;
    }

    @GetMapping
    public List<AnimalDTO> listar() {
        return service.listarTodos(); // Chama o service
    }

    @PostMapping
    public Animal cadastrar(@RequestBody Animal animal) {
        return service.salvar(animal);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id); // O erro do DELETE deve sumir aqui
    }
}
