package com.example.estudo_patudos_api_spring_jpa.controller;

import com.example.estudo_patudos_api_spring_jpa.model.Pet;
import com.example.estudo_patudos_api_spring_jpa.service.FavoritoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Favoritos exigem usuário autenticado (USER ou ADMIN) — coberto pelo
// .anyRequest().authenticated() do SecurityConfig.
@RestController
@RequestMapping("/api/favoritos")
public class FavoritoController {

    private final FavoritoService favoritoService;

    public FavoritoController(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    // Pets favoritados do usuário atual (objetos Pet completos, usados no perfil
    // e para acender os corações no front).
    @GetMapping
    public ResponseEntity<List<Pet>> listar(Authentication auth) {
        return ResponseEntity.ok(favoritoService.listar(auth.getName()));
    }

    // Toggle do favorito. Retorna o novo estado para o front atualizar o coração.
    @PostMapping("/{petId}")
    public ResponseEntity<ToggleResponse> toggle(Authentication auth, @PathVariable Long petId) {
        boolean favorito = favoritoService.toggle(auth.getName(), petId);
        return ResponseEntity.ok(new ToggleResponse(petId, favorito));
    }

    public record ToggleResponse(Long petId, boolean favorito) {}
}
