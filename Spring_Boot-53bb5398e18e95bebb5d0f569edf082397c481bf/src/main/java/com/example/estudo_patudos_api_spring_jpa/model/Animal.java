package com.example.estudo_patudos_api_spring_jpa.model;

import jakarta.persistence.*;
import lombok.Data; // O Lombok gera Getters/Setters sozinho!

@Entity // Diz: "Isso vai ser uma tabela no banco"
@Data   // Diz: "Lombok, crie os métodos básicos para mim"
public class Animal {

    @Id // Define que este campo é a Chave Primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // O banco gera o ID (1, 2, 3...)
    private Long id;

    private String nome;
    private String especie;
    private Integer idade;
    private String urlFoto;

    @ManyToOne // Muitos animais para uma instituição
    @JoinColumn(name = "instituicao_id") // Nome da coluna que será a FK no banco
    private Instituicao instituicao;
}

