package com.example.estudo_patudos_api_spring_jpa.controller;

import com.example.estudo_patudos_api_spring_jpa.model.Pet;
import com.example.estudo_patudos_api_spring_jpa.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "*") // Permite a comunicação com o Front-end
public class PetController {

    @Autowired
    private PetService petService;

    @GetMapping
    public ResponseEntity<Page<Pet>> listar(
            @RequestParam(required = false) String especie,
            @RequestParam(required = false) String genero,
            @RequestParam(required = false) String idade,
            @RequestParam(required = false) String status,
            Pageable pageable) {

        // O status padrão como "disponivel" garante que a Home funcione
        Page<Pet> pets = petService.listarTodos(especie, genero, idade, status,false, pageable);
        return ResponseEntity.ok(pets);
    }

    @PostMapping
    public ResponseEntity<Pet> cadastrarPet(@RequestBody Pet pet) {
        return ResponseEntity.status(HttpStatus.CREATED).body(petService.salvar(pet));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirPet(@PathVariable Long id) {
        petService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/adotado")
    public ResponseEntity<Void> adotar(@PathVariable Long id) {
        petService.marcarComoAdotado(id);
        return ResponseEntity.noContent().build();
    }

}