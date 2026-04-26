package com.example.estudo_patudos_api_spring_jpa.controller;

import com.example.estudo_patudos_api_spring_jpa.model.Instituicao;
import com.example.estudo_patudos_api_spring_jpa.service.InstituicaoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/instituicoes") // Rota diferente!
public class InstituicaoController {

    private final InstituicaoService service;

    public InstituicaoController(InstituicaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Instituicao> listar() {
        return service.listarTodas();
    }

    @PostMapping
    public Instituicao cadastrar(@RequestBody Instituicao instituicao) {
        return service.salvar(instituicao);
    }
}
