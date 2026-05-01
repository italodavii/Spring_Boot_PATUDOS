package com.example.estudo_patudos_api_spring_jpa.service;

import com.example.estudo_patudos_api_spring_jpa.model.Evento;
import com.example.estudo_patudos_api_spring_jpa.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    public List<Evento> listarTodos() {
        return eventoRepository.findAll();
    }

    public Evento salvar(Evento evento) {
        return eventoRepository.save(evento);
    }

    public void excluir(Long id) {
        if (eventoRepository.existsById(id)) {
            eventoRepository.deleteById(id);
        } else {
            throw new RuntimeException("Evento não encontrado");
        }
    }
}
