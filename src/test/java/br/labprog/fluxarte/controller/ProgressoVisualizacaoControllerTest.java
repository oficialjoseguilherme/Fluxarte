package br.labprog.fluxarte.controller;

import br.labprog.fluxarte.dto.request.ProgressoVisualizacaoRequest;
import br.labprog.fluxarte.dto.response.ProgressoVisualizacaoResponse;
import br.labprog.fluxarte.security.JwtService;
import br.labprog.fluxarte.service.ProgressoVisualizacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProgressoVisualizacaoController.class)
class ProgressoVisualizacaoControllerTest {

    private static final String URL = "/api/progresso";

    private static final UUID USUARIO_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final Long MIDIA_ID = 5L;
    private static final Long OBRA_ID = 10L;
    private static final Integer DURACAO_TOTAL_SEGUNDOS = 3600;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProgressoVisualizacaoService progressoService;

    // O JwtFilter faz parte da fatia web e exige um JwtService no construtor.
    @MockitoBean
    private JwtService jwtService;

    // ======================================================================
    // POST /api/progresso  (salvarProgresso)
    // Serviço retorna Optional: com valor -> 200 com corpo; vazio -> 204 sem corpo.
    // ======================================================================

    @Test
    void salvarProgresso_comDadosValidosEServicoRetornandoProgresso_deveRetornar200ComProgresso() throws Exception {
        ProgressoVisualizacaoRequest request = progressoRequestPadrao();
        when(progressoService.salvarProgresso(USUARIO_ID, request))
                .thenReturn(Optional.of(progressoResponse(1L, request)));

        mockMvc.perform(post(URL)
                        .with(usuario())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.midiaId").value(5))
                .andExpect(jsonPath("$.posicaoSegundos").value(request.posicaoSegundos()))
                .andExpect(jsonPath("$.duracaoTotalSegundos").value(3600))
                .andExpect(jsonPath("$.percentualAssistido").value(50.0));

        verify(progressoService).salvarProgresso(USUARIO_ID, request);
        verifyNoMoreInteractions(progressoService);
    }

    @Test
    void salvarProgresso_quandoServicoRetornaVazio_deveRetornar204SemCorpo() throws Exception {
        ProgressoVisualizacaoRequest request = progressoRequestPadrao();
        when(progressoService.salvarProgresso(USUARIO_ID, request)).thenReturn(Optional.empty());

        mockMvc.perform(post(URL)
                        .with(usuario())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(progressoService).salvarProgresso(USUARIO_ID, request);
        verifyNoMoreInteractions(progressoService);
    }

    @Test
    void salvarProgresso_comPosicaoZero_deveRetornar200() throws Exception {
        // @PositiveOrZero aceita 0 (início da mídia).
        ProgressoVisualizacaoRequest request = progressoRequest(MIDIA_ID, 0);
        when(progressoService.salvarProgresso(USUARIO_ID, request))
                .thenReturn(Optional.of(progressoResponse(1L, request)));

        mockMvc.perform(post(URL)
                        .with(usuario())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posicaoSegundos").value(0))
                .andExpect(jsonPath("$.percentualAssistido").value(0.0));

        verify(progressoService).salvarProgresso(USUARIO_ID, request);
    }

    @Test
    void salvarProgresso_comJsonMalformado_deveRetornar400() throws Exception {
        mockMvc.perform(post(URL)
                        .with(usuario())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ isso nao e json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(progressoService);
    }

    @Test
    void salvarProgresso_comCorpoVazio_deveRetornar400() throws Exception {
        // "{}" viola @NotNull em midiaId e posicaoSegundos.
        mockMvc.perform(post(URL)
                        .with(usuario())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(progressoService);
    }

    @Test
    void salvarProgresso_semMidiaId_deveRetornar400() throws Exception {
        ProgressoVisualizacaoRequest request = progressoRequest(null, 1800);

        mockMvc.perform(post(URL)
                        .with(usuario())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(progressoService);
    }

    @Test
    void salvarProgresso_comPosicaoNegativa_deveRetornar400() throws Exception {
        ProgressoVisualizacaoRequest request = progressoRequest(MIDIA_ID, -1);

        mockMvc.perform(post(URL)
                        .with(usuario())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(progressoService);
    }

    @Test
    void salvarProgresso_comPosicaoNaoNumerica_deveRetornar400() throws Exception {
        mockMvc.perform(post(URL)
                        .with(usuario())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"midiaId\": 5, \"posicaoSegundos\": \"abc\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(progressoService);
    }

    // ======================================================================
    // GET /api/progresso  (listarMeuProgresso)
    // ======================================================================

    @Test
    void listarMeuProgresso_comProgressos_deveRetornar200ComLista() throws Exception {
        when(progressoService.listarPorUsuario(USUARIO_ID)).thenReturn(List.of(
                progressoResponse(1L, 5L, 1800),
                progressoResponse(2L, 6L, 900)));

        mockMvc.perform(get(URL)
                        .with(usuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", contains(1, 2)))
                .andExpect(jsonPath("$[*].midiaId", contains(5, 6)))
                .andExpect(jsonPath("$[*].posicaoSegundos", contains(1800, 900)));

        verify(progressoService).listarPorUsuario(USUARIO_ID);
        verifyNoMoreInteractions(progressoService);
    }

    @Test
    void listarMeuProgresso_semResultados_deveRetornar200ComListaVazia() throws Exception {
        when(progressoService.listarPorUsuario(USUARIO_ID)).thenReturn(List.of());

        mockMvc.perform(get(URL)
                        .with(usuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(progressoService).listarPorUsuario(USUARIO_ID);
        verifyNoMoreInteractions(progressoService);
    }

    // ======================================================================
    // GET /api/progresso/{midiaId}  (buscarProgresso)
    // ======================================================================

    @Test
    void buscarProgresso_comProgressoExistente_deveRetornar200ComProgresso() throws Exception {
        when(progressoService.buscarProgresso(USUARIO_ID, MIDIA_ID))
                .thenReturn(Optional.of(progressoResponse(1L, MIDIA_ID, 1800)));

        mockMvc.perform(get(URL + "/{midiaId}", MIDIA_ID)
                        .with(usuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.obraId").value(10))
                .andExpect(jsonPath("$.midiaId").value(5))
                .andExpect(jsonPath("$.posicaoSegundos").value(1800))
                .andExpect(jsonPath("$.percentualAssistido").value(50.0));

        verify(progressoService).buscarProgresso(USUARIO_ID, MIDIA_ID);
        verifyNoMoreInteractions(progressoService);
    }

    @Test
    void buscarProgresso_quandoServicoRetornaVazio_deveRetornar404SemCorpo() throws Exception {
        when(progressoService.buscarProgresso(USUARIO_ID, 99L)).thenReturn(Optional.empty());

        mockMvc.perform(get(URL + "/{midiaId}", 99L)
                        .with(usuario()))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        verify(progressoService).buscarProgresso(USUARIO_ID, 99L);
        verifyNoMoreInteractions(progressoService);
    }

    @Test
    void buscarProgresso_comMidiaIdNaoNumerico_deveRetornar400() throws Exception {
        mockMvc.perform(get(URL + "/{midiaId}", "abc")
                        .with(usuario()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(progressoService);
    }

    // ======================================================================
    // DELETE /api/progresso/{midiaId}  (removerProgresso)
    // ======================================================================

    @Test
    void removerProgresso_comMidiaIdValido_deveRetornar204SemCorpo() throws Exception {
        mockMvc.perform(delete(URL + "/{midiaId}", MIDIA_ID)
                        .with(usuario())
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(progressoService).remover(USUARIO_ID, MIDIA_ID);
        verifyNoMoreInteractions(progressoService);
    }

    @Test
    void removerProgresso_comMidiaIdNaoNumerico_deveRetornar400() throws Exception {
        mockMvc.perform(delete(URL + "/{midiaId}", "abc")
                        .with(usuario())
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(progressoService);
    }

    // ======================================================================
    // Auxiliares
    // ======================================================================

    /**
     * Usuário autenticado cujo principal é o UUID esperado por @AuthenticationPrincipal UUID,
     * para os endpoints anotados com isAuthenticated().
     */
    private static RequestPostProcessor usuario() {
        return authentication(new UsernamePasswordAuthenticationToken(USUARIO_ID, null, List.of()));
    }

    // ---- Únicos pontos que dependem dos DTOs ----

    private ProgressoVisualizacaoRequest progressoRequest(Long midiaId, Integer posicaoSegundos) {
        return new ProgressoVisualizacaoRequest(midiaId, posicaoSegundos);
    }

    private ProgressoVisualizacaoRequest progressoRequestPadrao() {
        return progressoRequest(MIDIA_ID, 1800);
    }

    /** Response coerente com o request: midiaId e posicaoSegundos copiados; demais campos são exclusivos do Response. */
    private ProgressoVisualizacaoResponse progressoResponse(Long id, ProgressoVisualizacaoRequest request) {
        return progressoResponse(id, request.midiaId(), request.posicaoSegundos());
    }

    private ProgressoVisualizacaoResponse progressoResponse(Long id, Long midiaId, Integer posicaoSegundos) {
        return new ProgressoVisualizacaoResponse(
                id,
                OBRA_ID,
                "Documentário de Exemplo",
                "https://exemplo.com/poster.png",
                midiaId,
                posicaoSegundos,
                DURACAO_TOTAL_SEGUNDOS,
                posicaoSegundos * 100.0 / DURACAO_TOTAL_SEGUNDOS
        );
    }
}