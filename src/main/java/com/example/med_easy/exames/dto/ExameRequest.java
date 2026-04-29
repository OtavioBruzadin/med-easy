package com.example.med_easy.exames.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.HashMap;

public record ExameRequest(
        @NotNull LocalDate dataExame,
        @NotBlank String cpfPaciente,
        @NotBlank String nomePaciente,
        @NotBlank String tipoExame,
        @NotNull HashMap<String, Double> resultados
) {
}