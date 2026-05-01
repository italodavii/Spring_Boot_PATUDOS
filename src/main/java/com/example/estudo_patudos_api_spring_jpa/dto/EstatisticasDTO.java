package com.example.estudo_patudos_api_spring_jpa.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstatisticasDTO {
    private long totalPetsAtivos;
    private long petsAdotados;
    private long petsDisponiveis;
    private long totalPetsHistorico;
    private long eventosAgendados;
    private long totalEventosHistorico;
}
