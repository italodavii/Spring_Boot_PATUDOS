package com.example.estudo_patudos_api_spring_jpa.service;

import com.example.estudo_patudos_api_spring_jpa.dto.EstatisticasDTO;
import com.example.estudo_patudos_api_spring_jpa.dto.PublicStatsDTO;
import com.example.estudo_patudos_api_spring_jpa.repository.EventoRepository;
import com.example.estudo_patudos_api_spring_jpa.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class EstatisticasService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private EventoRepository eventoRepository;

    public PublicStatsDTO gerarRelatorioPublico() {
        PublicStatsDTO dto = new PublicStatsDTO();
        long adotados = petRepository.countByStatus("adotado");
        long disponiveis = petRepository.countByAtivoTrueAndStatus("disponivel");

        dto.setPetsAdotados(adotados);
        dto.setPetsDisponiveis(disponiveis);
        dto.setTotalPetsHistorico(adotados + disponiveis);
        return dto;
    }

    public EstatisticasDTO gerarDashboardCompleto() {
        EstatisticasDTO dto = new EstatisticasDTO();
        dto.setPetsDisponiveis(petRepository.countByAtivoTrueAndStatus("disponivel"));
        dto.setPetsAdotados(petRepository.countByStatus("adotado"));
        dto.setTotalPetsAtivos(petRepository.countByAtivoTrue());
        dto.setTotalPetsHistorico(petRepository.count());

        // Eventos
        LocalDate hoje = LocalDate.now();
        dto.setEventosAgendados(eventoRepository.countByDataAfter(hoje));
        dto.setTotalEventosFinalizados(eventoRepository.countByDataBefore(hoje));

        return dto;
    }
}