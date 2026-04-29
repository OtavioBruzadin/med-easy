package com.example.med_easy.exames.dto;

import java.time.LocalDate;
import java.util.Map;

public record ExameResponse(
        String id,
        LocalDate dataExame,
        String cpfPaciente,
        String nomePaciente,
        String tipoExame,
        Map<String, Double> resultados
) {
}