package com.example.estudo_patudos_api_spring_jpa.specifications;

import com.example.estudo_patudos_api_spring_jpa.model.Pet;
import org.springframework.data.jpa.domain.Specification;

public class PetSpecifications {

    public static Specification<Pet> comEspecie(String especie) {
        return (root, query, cb) -> {
            if (especie == null || especie.isBlank()) return null;
            // Usa lower para ignorar maiúsculas e trim para ignorar espaços
            return cb.equal(cb.lower(root.get("especie")), especie.toLowerCase().trim());
        };
    }

    public static Specification<Pet> comStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isBlank()) return null;
            return cb.equal(cb.lower(root.get("status")), status.toLowerCase().trim());
        };
    }

    public static Specification<Pet> comGenero(String genero) {
        return (root, query, cb) -> {
            if (genero == null || genero.isBlank()) return null;
            return cb.equal(cb.lower(root.get("genero")), genero.toLowerCase().trim());
        };
    }

    public static Specification<Pet> comIdade(String idade) {
        return (root, query, cb) -> {
            if (idade == null || idade.isBlank()) return null;
            return cb.equal(cb.lower(root.get("idade")), idade.toLowerCase().trim());
        };
    }
}