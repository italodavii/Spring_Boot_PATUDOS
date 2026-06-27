package com.example.estudo_patudos_api_spring_jpa.controller;

import com.example.estudo_patudos_api_spring_jpa.dto.EstatisticasDTO;
import com.example.estudo_patudos_api_spring_jpa.dto.PublicStatsDTO;
import com.example.estudo_patudos_api_spring_jpa.repository.PetRepository;
import com.example.estudo_patudos_api_spring_jpa.service.EstatisticasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/estatisticas")
public class EstatisticasController {

    @Autowired
    private EstatisticasService estatisticasService; // Injetando o Service

    @GetMapping("/publico")
    public ResponseEntity<PublicStatsDTO> obterEstatisticasPublicas() {
        return ResponseEntity.ok(estatisticasService.gerarRelatorioPublico());
    }

    @GetMapping("/dashboard")
    public ResponseEntity<EstatisticasDTO> obterDashboardCompleto() {
        return ResponseEntity.ok(estatisticasService.gerarDashboardCompleto());
    }
}