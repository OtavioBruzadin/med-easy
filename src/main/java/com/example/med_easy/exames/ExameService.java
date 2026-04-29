package com.example.med_easy.exames;

import com.example.med_easy.exames.dto.ExameRequest;
import com.example.med_easy.exames.dto.ExameResponse;
import com.example.med_easy.user.User;
import com.example.med_easy.user.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExameService {

    private final ExameRepository exameRepository;
    private final UserRepository userRepository;

    public ExameService(ExameRepository exameRepository, UserRepository userRepository) {
        this.exameRepository = exameRepository;
        this.userRepository = userRepository;
    }

    public ExameResponse salvarNovoExame(ExameRequest request) {
        User usuarioLogado = obterUsuarioAutenticado();

        Exame exame = new Exame();
        exame.setDataExame(request.dataExame());
        exame.setTipoExame(request.tipoExame().trim());
        exame.setResultados(request.resultados());
        exame.setCpfPaciente(request.cpfPaciente().trim());
        exame.setNomePaciente(request.nomePaciente().trim());
        exame.setUserId(usuarioLogado.getId());

        Exame salvo = exameRepository.save(exame);
        return toResponse(salvo);
    }

    public List<ExameResponse> procurarExames(String cpfPaciente, String nomePaciente, String tipoExame) {
        User usuarioLogado = obterUsuarioAutenticado();

        return exameRepository.findByUserId(usuarioLogado.getId())
                .stream()
                .filter(exame -> cpfPaciente == null || cpfPaciente.isBlank() ||
                        exame.getCpfPaciente() != null && exame.getCpfPaciente().toLowerCase().contains(cpfPaciente.toLowerCase()))
                .filter(exame -> nomePaciente == null || nomePaciente.isBlank() ||
                        exame.getNomePaciente() != null && exame.getNomePaciente().toLowerCase().contains(nomePaciente.toLowerCase()))
                .filter(exame -> tipoExame == null || tipoExame.isBlank() ||
                        exame.getTipoExame() != null && exame.getTipoExame().toLowerCase().contains(tipoExame.toLowerCase()))
                .map(this::toResponse)
                .toList();
    }

    private ExameResponse toResponse(Exame exame) {
        return new ExameResponse(
                exame.getId(),
                exame.getDataExame(),
                exame.getCpfPaciente(),
                exame.getNomePaciente(),
                exame.getTipoExame(),
                exame.getResultados()
        );
    }

    private User obterUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new BadCredentialsException("Usuário não autenticado");
        }

        String email = authentication.getName();

        return userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new BadCredentialsException("Usuário autenticado não encontrado"));
    }
}