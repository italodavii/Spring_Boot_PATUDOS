package com.example.estudo_patudos_api_spring_jpa.service;

import com.example.estudo_patudos_api_spring_jpa.model.Evento;
import com.example.estudo_patudos_api_spring_jpa.repository.EventoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    // Lista todos os eventos agendados (ativos + futuros) — uso na Home.
    public List<Evento> listarEventosFuturos() {
        return eventoRepository.findByDataGreaterThanEqualOrderByDataAsc(LocalDate.now());
    }

    // Versão paginada — uso no catálogo público e na lista admin.
    public Page<Evento> listarPaginado(Pageable pageable) {
        return eventoRepository.findByDataGreaterThanEqualOrderByDataAsc(LocalDate.now(), pageable);
    }

    public Evento salvar(Evento evento) {
        return eventoRepository.save(evento);
    }

    @Transactional
    public Evento atualizar(Long id, Evento dados) {
        Evento existente = eventoRepository.findById(id).orElseThrow();

        if (dados.getTitulo() != null) existente.setTitulo(dados.getTitulo().trim());
        if (dados.getDescricao() != null) existente.setDescricao(dados.getDescricao());
        if (dados.getEndereco() != null) existente.setEndereco(dados.getEndereco().trim());
        if (dados.getData() != null) existente.setData(dados.getData());
        if (dados.getHoraISO() != null) existente.setHoraISO(dados.getHoraISO());
        if (dados.getUrlFoto() != null && !dados.getUrlFoto().isBlank()) {
            existente.setUrlFoto(dados.getUrlFoto());
        }
        // ativo é preservado — transição só via excluir().
        return eventoRepository.save(existente);
    }

    // Soft delete: o @SQLDelete da entidade transforma o DELETE em UPDATE ativo=false.
    @Transactional
    public void excluir(Long id) {
        if (!eventoRepository.existsById(id)) {
            throw new RuntimeException("Evento não encontrado");
        }
        eventoRepository.deleteById(id);
    }
}
