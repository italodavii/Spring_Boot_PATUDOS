package com.example.estudo_patudos_api_spring_jpa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Instituicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cnpj;
    private String cidade;

    // Relacionamento @OneToMany: Uma instituição pode ter vários animais
    // O "mappedBy" diz que o controle da ligação está lá na classe Animal
    @OneToMany(mappedBy = "instituicao")
    @JsonIgnore // Isso impede que, ao listar a ONG, ela tente listar todos os animais de volta
    private List<Animal> animais;
}
