package com.example.estudo_patudos_api_spring_jpa.service;

import com.example.estudo_patudos_api_spring_jpa.model.Favorito;
import com.example.estudo_patudos_api_spring_jpa.model.Pet;
import com.example.estudo_patudos_api_spring_jpa.model.Usuario;
import com.example.estudo_patudos_api_spring_jpa.repository.FavoritoRepository;
import com.example.estudo_patudos_api_spring_jpa.repository.PetRepository;
import com.example.estudo_patudos_api_spring_jpa.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PetRepository petRepository;

    public FavoritoService(FavoritoRepository favoritoRepository,
                           UsuarioRepository usuarioRepository,
                           PetRepository petRepository) {
        this.favoritoRepository = favoritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.petRepository = petRepository;
    }

    // Pets favoritados do usuário (mais recentes primeiro). Carrega os pets pelos ids
    // via PetRepository, que aplica o @SQLRestriction("ativo = true") e descarta os
    // pets soft-deletados. A ordem (criadoEm desc) vem da projeção de ids.
    public List<Pet> listar(String email) {
        Usuario u = buscarUsuario(email);
        List<Long> ids = favoritoRepository.findPetIdsByUsuario(u);
        if (ids.isEmpty()) return List.of();

        Map<Long, Pet> porId = petRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Pet::getId, Function.identity()));
        return ids.stream()
                .map(porId::get)
                .filter(Objects::nonNull)
                .toList();
    }

    // Toggle: se já é favorito, remove e retorna false; senão adiciona e retorna true.
    // Assim o front dispara uma única ação independente do estado atual do coração.
    public boolean toggle(String email, Long petId) {
        Usuario u = buscarUsuario(email);
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado."));

        Optional<Favorito> existente = favoritoRepository.findByUsuarioAndPet(u, pet);
        if (existente.isPresent()) {
            favoritoRepository.delete(existente.get());
            return false;
        }
        Favorito f = new Favorito();
        f.setUsuario(u);
        f.setPet(pet);
        favoritoRepository.save(f);
        return true;
    }

    private Usuario buscarUsuario(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sessão inválida."));
    }
}
