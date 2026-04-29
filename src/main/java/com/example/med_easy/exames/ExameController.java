package com.example.med_easy.exames;

import com.example.med_easy.exames.dto.ExameRequest;
import com.example.med_easy.exames.dto.ExameResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exames")
public class ExameController {

    private final ExameService exameService;

    public ExameController(ExameService exameService) {
        this.exameService = exameService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExameResponse salvar(@Valid @RequestBody ExameRequest request) {
        return exameService.salvarNovoExame(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ExameResponse> procurar(
            @RequestParam(required = false) String cpfPaciente,
            @RequestParam(required = false) String nomePaciente,
            @RequestParam(required = false) String tipoExame
    ) {
        return exameService.procurarExames(cpfPaciente, nomePaciente, tipoExame);
    }
}