package com.example.estudo_patudos_api_spring_jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicStatsDTO {
    private long petsAdotados;
    private long petsDisponiveis;
    private long totalPetsHistorico;
}
