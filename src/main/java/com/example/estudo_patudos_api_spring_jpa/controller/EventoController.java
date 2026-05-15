package com.example.estudo_patudos_api_spring_jpa.controller;

import com.example.estudo_patudos_api_spring_jpa.model.Evento;
import com.example.estudo_patudos_api_spring_jpa.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
@CrossOrigin(origins = "http://localhost:5173")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    // GET /api/eventos             -> lista simples (Home: usa ?size=3 implicitamente no front)
    // GET /api/eventos?paged=true  -> Page<Evento> (catálogo / dashboard admin)
    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(required = false, defaultValue = "false") boolean paged,
            Pageable pageable) {
        if (paged) {
            Page<Evento> page = eventoService.listarPaginado(pageable);
            return ResponseEntity.ok(page);
        }
        List<Evento> eventos = eventoService.listarEventosFuturos();
        return ResponseEntity.ok(eventos);
    }

    @PostMapping
    public ResponseEntity<Evento> cadastrar(@RequestBody Evento evento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoService.salvar(evento));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> atualizar(@PathVariable Long id, @RequestBody Evento dados) {
        return ResponseEntity.ok(eventoService.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        eventoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
