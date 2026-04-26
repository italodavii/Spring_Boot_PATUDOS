package com.example.estudo_patudos_api_spring_jpa.dto;

public record AnimalDTO (Long id, String nome, String especie, String nomeInstituicao) {
    // Aqui será entegue o nome da Instituição e não o objeto!
}