package br.labprog.fluxarte.controller;


import br.labprog.fluxarte.dto.request.ObraAudiovisualRequest;
import br.labprog.fluxarte.dto.response.ObraAudiovisualResponse;
import br.labprog.fluxarte.exceptions.RecursoNaoEncontradoException;
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
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
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

    @Test
    void listarTodas_deveRetornarTodasAsObras() throws Exception {
        // ---------- Shrek ----------
        String tituloShrek = "Shrek";
        String tituloOriginalShrek = "Shrek";
        String diretorShrek = "Andrew Adamson e Vicky Jenson";
        String sinopseShrek = "Um ogro rabugento sai em missão para recuperar o seu pântano.";
        int anoProducaoShrek = 2001;
        String idiomaOriginalShrek = "Inglês";
        String posterUrlShrek = "posterUrlShrek";
        String bannerUrlShrek = "bannerUrlShrek";
        boolean disponivelShrek = true;
        StatusObra statusShrek = StatusObra.ATIVO;
        LocalDateTime dataInicioExibicaoShrek = LocalDateTime.of(2026, 11, 1, 20, 0);
        LocalDateTime dataFimExibicaoShrek = LocalDateTime.of(2027, 11, 1, 22, 0);
        ClassificacaoIndicativa classificacaoIndicativaShrek = ClassificacaoIndicativa.LIVRE;
        Set<NomeGenero> generosShrek = Set.of(NomeGenero.ANIMACAO);

        Long idShrek = 2L;
        ObraAudiovisualResponse responseShrek = new ObraAudiovisualResponse(
                idShrek,
                tituloShrek,
                tituloOriginalShrek,
                diretorShrek,
                sinopseShrek,
                anoProducaoShrek,
                idiomaOriginalShrek,
                posterUrlShrek,
                bannerUrlShrek,
                dataInicioExibicaoShrek,
                dataFimExibicaoShrek,
                classificacaoIndicativaShrek,
                generosShrek,
                disponivelShrek
        );

        String tituloDoc = "Free Solo";
        String tituloOriginalDoc = "Free Solo";
        String diretorDoc = "Elizabeth Chai Vasarhelyi e Jimmy Chin";
        String sinopseDoc = "Um escalador tenta subir uma parede gigante sem corda e sem proteção.";
        int anoProducaoDoc = 2018;
        String idiomaOriginalDoc = "Inglês";
        String posterUrlDoc = "posterUrlDoc";
        String bannerUrlDoc = "bannerUrlDoc";
        boolean disponivelDoc = true;
        StatusObra statusDoc = StatusObra.ATIVO;
        LocalDateTime dataInicioExibicaoDoc = LocalDateTime.of(2026, 12, 5, 19, 0);
        LocalDateTime dataFimExibicaoDoc = LocalDateTime.of(2027, 12, 5, 21, 0);
        ClassificacaoIndicativa classificacaoIndicativaDoc = ClassificacaoIndicativa.LIVRE;
        Set<NomeGenero> generosDoc = Set.of(NomeGenero.DOCUMENTARIO);

        Long idDoc = 3L;
        ObraAudiovisualResponse responseDoc = new ObraAudiovisualResponse(
                idDoc,
                tituloDoc,
                tituloOriginalDoc,
                diretorDoc,
                sinopseDoc,
                anoProducaoDoc,
                idiomaOriginalDoc,
                posterUrlDoc,
                bannerUrlDoc,
                dataInicioExibicaoDoc,
                dataFimExibicaoDoc,
                classificacaoIndicativaDoc,
                generosDoc,
                disponivelDoc
        );

        when(obraService.listarTodas()).thenReturn(List.of(obraAudiovisualResponsePadrao(), responseShrek, responseDoc));

        mockMvc.perform(get("/api/obras")
                        .with(user("usuario")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Toy Story"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].titulo").value("Shrek"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].titulo").value("Free Solo"));
    }

    @Test
    void buscarPorId_comIdExistente_deveRetornar200ComAObra() throws Exception {
        when(obraService.buscarPorId(1L)).thenReturn(obraAudiovisualResponsePadrao());

        mockMvc.perform(get("/api/obras/{id}", 1)
                        .with(user("usuario")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.titulo").value("Toy Story"))
                .andExpect(jsonPath("$.diretor").value("John Lasseter"));
    }

    @Test
    void buscarPorId_comIdInexistente_deveRetornar404() throws Exception {
        when(obraService.buscarPorId(13L)).thenThrow(new RecursoNaoEncontradoException("Obra não encontrada."));

        mockMvc.perform(get("/api/obras/{id}", 13L)
                        .with(user("usuario")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Obra não encontrada."));
    }

    private ObraAudiovisualResponse obraAudiovisualResponsePadrao() {
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

        return response;
    }
}
