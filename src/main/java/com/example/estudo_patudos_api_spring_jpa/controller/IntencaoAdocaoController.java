package com.example.estudo_patudos_api_spring_jpa.controller;

import com.example.estudo_patudos_api_spring_jpa.dto.intencao.CriarIntencaoRequest;
import com.example.estudo_patudos_api_spring_jpa.dto.intencao.IntencaoDTO;
import com.example.estudo_patudos_api_spring_jpa.model.StatusIntencao;
import com.example.estudo_patudos_api_spring_jpa.service.IntencaoAdocaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/intencoes")
public class IntencaoAdocaoController {

    private final IntencaoAdocaoService service;

    public IntencaoAdocaoController(IntencaoAdocaoService service) {
        this.service = service;
    }

    // USER autenticado envia a intenção (termo + entrevista). Matchers no SecurityConfig.
    @PostMapping
    public ResponseEntity<IntencaoDTO> criar(Authentication auth, @RequestBody CriarIntencaoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(auth.getName(), req));
    }

    // USER: minhas próprias intenções (perfil, somente leitura).
    @GetMapping("/minhas")
    public ResponseEntity<List<IntencaoDTO>> minhas(Authentication auth) {
        return ResponseEntity.ok(service.listarMinhas(auth.getName()));
    }

    // ADMIN: lista todas as fichas (mais recentes primeiro).
    @GetMapping
    public ResponseEntity<List<IntencaoDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    // ADMIN: badge de pendentes na sidebar.
    @GetMapping("/pendentes/count")
    public ResponseEntity<Map<String, Long>> contarPendentes() {
        return ResponseEntity.ok(Map.of("count", service.contarPendentes()));
    }

    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<IntencaoDTO> aprovar(@PathVariable Long id) {
        return ResponseEntity.ok(service.definirStatus(id, StatusIntencao.APROVADA));
    }

    @PatchMapping("/{id}/recusar")
    public ResponseEntity<IntencaoDTO> recusar(@PathVariable Long id) {
        return ResponseEntity.ok(service.definirStatus(id, StatusIntencao.RECUSADA));
    }
}
