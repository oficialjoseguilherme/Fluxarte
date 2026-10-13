package br.labprog.fluxarte.controller;


import br.labprog.fluxarte.dto.request.ObraAudiovisualRequest;
import br.labprog.fluxarte.dto.response.ObraAudiovisualResponse;
import br.labprog.fluxarte.model.GeneroObra;
import br.labprog.fluxarte.model.enums.ClassificacaoIndicativa;
import br.labprog.fluxarte.model.enums.NomeGenero;
import br.labprog.fluxarte.model.enums.StatusObra;
import br.labprog.fluxarte.security.JwtService;
import br.labprog.fluxarte.service.ObraAudiovisualService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ObraAudiovisualController.class)
class ObraAudiovisualControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ObraAudiovisualService obraService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void cadastrar_comDadosValidos_deveRetornar201ComObraCriada() throws Exception {
        String titulo = "Toy Story";
        String tituloOriginal = "Toy Story";
        String diretor = "John Lasseter";
        String sinopse = "Brinquedos ganham vida e olha no que deu.";
        int anoProducao = 1995;
        String idiomaOriginal = "Inglês";
        String posterUrl = "posterUrl";
        String bannerUrl = "bannerUrl";
        boolean disponivel = true;
        StatusObra status = StatusObra.ATIVO;
        LocalDateTime dataInicioExibicao = LocalDateTime.of(2026, 10, 25, 8, 0);
        LocalDateTime dataFimExibicao = LocalDateTime.of(2027, 10, 25, 17, 0);
        ClassificacaoIndicativa classificacaoIndicativa = ClassificacaoIndicativa.LIVRE;
        Set<NomeGenero> generos = Set.of(NomeGenero.ANIMACAO);

        ObraAudiovisualRequest request = new ObraAudiovisualRequest(
                titulo,
                tituloOriginal,
                diretor,
                sinopse,
                anoProducao,
                idiomaOriginal,
                posterUrl,
                bannerUrl,
                disponivel,
                status,
                dataInicioExibicao,
                dataFimExibicao,
                classificacaoIndicativa,
                generos
        );
        Long id = 1L;
        ObraAudiovisualResponse response = new ObraAudiovisualResponse(
                id,
                titulo,
                tituloOriginal,
                diretor,
                sinopse,
                anoProducao,
                idiomaOriginal,
                posterUrl,
                bannerUrl,
                dataInicioExibicao,
                dataFimExibicao,
                classificacaoIndicativa,
                generos,
                disponivel
        );

        when(obraService.cadastrar(request)).thenReturn(response);

        mockMvc.perform(post("/api/obras")
                        .with(user("usurario"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.titulo").value(titulo))
                .andExpect(jsonPath("$.tituloOriginal").value(tituloOriginal))
                .andExpect(jsonPath("$.diretor").value(diretor))
                .andExpect(jsonPath("$.sinopse").value(sinopse))
                .andExpect(jsonPath("$.anoProducao").value(anoProducao))
                .andExpect(jsonPath("$.idiomaOriginal").value(idiomaOriginal))
                .andExpect(jsonPath("$.posterUrl").value(posterUrl))
                .andExpect(jsonPath("$.bannerUrl").value(bannerUrl))
                .andExpect(jsonPath("$.dataInicioExibicao").value(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dataInicioExibicao)))
                .andExpect(jsonPath("$.dataFimExibicao").value(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dataFimExibicao)))
                .andExpect(jsonPath("$.classificacaoIndicativa").value(classificacaoIndicativa.name()))
                .andExpect(jsonPath("$.generos", containsInAnyOrder(
                        generos.stream().map(Enum::name).toArray(String[]::new))))
                .andExpect(jsonPath("$.exibivel").value(disponivel));

    }
}
