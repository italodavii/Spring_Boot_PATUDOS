package com.example.estudo_patudos_api_spring_jpa.controller;

import com.example.estudo_patudos_api_spring_jpa.model.Evento;
import com.example.estudo_patudos_api_spring_jpa.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
@CrossOrigin(origins = "*")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @GetMapping
    public ResponseEntity<List<Evento>> listar() {
        return ResponseEntity.ok(eventoService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<Evento> cadastrar(@RequestBody Evento evento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoService.salvar(evento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        eventoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
