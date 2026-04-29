package com.example.med_easy.exames;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Document(collection = "exames")
public class Exame {
    @Id
    private String id;
    private LocalDate dataExame;
    private String userId;
    private String cpfPaciente;
    private String nomePaciente;
    private String tipoExame;
    private HashMap<String, Double> resultados;

    public Exame() {
    }

    public Exame(LocalDate dataExame, String tipoExame, HashMap<String, Double> resultados, String userId, String cpfPaciente, String nomePaciente) {
        this.dataExame = dataExame;
        this.tipoExame = tipoExame;
        this.resultados = resultados;
        this.userId = userId;
        this.cpfPaciente = cpfPaciente;
        this.nomePaciente = nomePaciente;
    }

    public String getId() {
        return id;
    }

    public LocalDate getDataExame() {
        return dataExame;
    }

    public String getUserId() {
        return userId;
    }

    public String getCpfPaciente() {
        return cpfPaciente;
    }

    public String getNomePaciente() {
        return nomePaciente;
    }

    public String getTipoExame() {
        return tipoExame;
    }

    public Map<String, Double> getResultados() {
        return resultados;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDataExame(LocalDate dataExame) {
        this.dataExame = dataExame;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCpfPaciente(String cpfPaciente) {
        this.cpfPaciente = cpfPaciente;
    }

    public void setNomePaciente(String nomePaciente) {
        this.nomePaciente = nomePaciente;
    }

    public void setTipoExame(String tipoExame) {
        this.tipoExame = tipoExame;
    }

    public void setResultados(HashMap<String, Double> resultados) {
        this.resultados = resultados;
    }
}