package com.example.med_easy.exames;

import com.example.med_easy.exames.dto.ExameRequest;
import com.example.med_easy.exames.dto.ExameResponse;
import com.example.med_easy.user.User;
import com.example.med_easy.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExameServiceTest {

    @Mock
    private ExameRepository exameRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExameService exameService;

    private User userLogado;

    @BeforeEach
    void setup() {
        userLogado = new User("Otavio", "otavio@email.com", "hash", Instant.now());
        userLogado.setId("user-123");
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    class SalvarNovoExame {

        @Test
        void deveSalvarERetornarResponse() {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken("otavio@email.com", null)
            );

            when(userRepository.findByEmailIgnoreCase("otavio@email.com"))
                    .thenReturn(Optional.of(userLogado));

            HashMap<String, Double> resultados = new HashMap<>();
            resultados.put("hemoglobina", 13.5);

            ExameRequest request = new ExameRequest(
                    LocalDate.of(2026, 4, 20),
                    " 12345678900 ",
                    " Maria ",
                    " Hemograma ",
                    resultados
            );

            Exame exameSalvo = new Exame();
            exameSalvo.setId("ex-1");
            exameSalvo.setDataExame(request.dataExame());
            exameSalvo.setCpfPaciente("12345678900");
            exameSalvo.setNomePaciente("Maria");
            exameSalvo.setTipoExame("Hemograma");
            exameSalvo.setResultados(resultados);
            exameSalvo.setUserId("user-123");

            when(exameRepository.save(any(Exame.class))).thenReturn(exameSalvo);

            ExameResponse response = exameService.salvarNovoExame(request);

            ArgumentCaptor<Exame> captor = ArgumentCaptor.forClass(Exame.class);
            verify(exameRepository).save(captor.capture());
            Exame enviadoParaSalvar = captor.getValue();

            assertEquals("user-123", enviadoParaSalvar.getUserId());
            assertEquals("12345678900", enviadoParaSalvar.getCpfPaciente());
            assertEquals("Maria", enviadoParaSalvar.getNomePaciente());
            assertEquals("Hemograma", enviadoParaSalvar.getTipoExame());

            assertEquals("ex-1", response.id());
            assertEquals("12345678900", response.cpfPaciente());
            assertEquals("Maria", response.nomePaciente());
            assertEquals("Hemograma", response.tipoExame());
            assertEquals(13.5, response.resultados().get("hemoglobina"));
        }

        @Test
        void deveLancarExcecaoQuandoUsuarioNaoAutenticado() {
            SecurityContextHolder.clearContext();

            HashMap<String, Double> resultados = new HashMap<>();
            resultados.put("x", 1.0);

            ExameRequest request = new ExameRequest(
                    LocalDate.now(),
                    "12345678900",
                    "Maria",
                    "Hemograma",
                    resultados
            );

            assertThrows(BadCredentialsException.class, () -> exameService.salvarNovoExame(request));
            verifyNoInteractions(exameRepository);
        }
    }

    @Nested
    class ProcurarExames {

        @Test
        void deveFiltrarPorCpfNomeETipo() {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken("otavio@email.com", null)
            );

            when(userRepository.findByEmailIgnoreCase("otavio@email.com"))
                    .thenReturn(Optional.of(userLogado));

            Exame e1 = new Exame();
            e1.setId("1");
            e1.setUserId("user-123");
            e1.setCpfPaciente("12345678900");
            e1.setNomePaciente("Maria Silva");
            e1.setTipoExame("Hemograma");
            e1.setDataExame(LocalDate.now());
            e1.setResultados(new HashMap<>());

            Exame e2 = new Exame();
            e2.setId("2");
            e2.setUserId("user-123");
            e2.setCpfPaciente("99999999999");
            e2.setNomePaciente("João");
            e2.setTipoExame("Glicemia");
            e2.setDataExame(LocalDate.now());
            e2.setResultados(new HashMap<>());

            when(exameRepository.findByUserId("user-123")).thenReturn(List.of(e1, e2));

            List<ExameResponse> resultado = exameService.procurarExames("123", "maria", "hemo");

            assertEquals(1, resultado.size());
            assertEquals("1", resultado.getFirst().id());
            assertEquals("Maria Silva", resultado.getFirst().nomePaciente());
        }

        @Test
        void deveLancarExcecaoQuandoUsuarioAutenticadoNaoEncontrado() {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken("naoexiste@email.com", null)
            );

            when(userRepository.findByEmailIgnoreCase("naoexiste@email.com"))
                    .thenReturn(Optional.empty());

            assertThrows(BadCredentialsException.class,
                    () -> exameService.procurarExames(null, null, null));

            verify(exameRepository, never()).findByUserId(anyString());
        }
    }
}