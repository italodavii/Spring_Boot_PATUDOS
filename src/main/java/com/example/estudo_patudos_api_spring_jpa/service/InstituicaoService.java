package com.example.estudo_patudos_api_spring_jpa.service;

import com.example.estudo_patudos_api_spring_jpa.model.Instituicao;
import com.example.estudo_patudos_api_spring_jpa.repository.InstituicaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstituicaoService {

    private final InstituicaoRepository repository;

    public InstituicaoService(InstituicaoRepository repository) {
        this.repository = repository;
    }

    public List<Instituicao> listarTodas() {
        return repository.findAll();
    }

    public Instituicao salvar(Instituicao instituicao) {
        // REGRA DE NEGÓCIO: Aqui o "copia e cola" muda
        if (instituicao.getCnpj() == null || instituicao.getCnpj().length() < 14) {
            throw new RuntimeException("CNPJ inválido!");
        }
        return repository.save(instituicao);
    }
}