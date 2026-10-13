package br.labprog.fluxarte.controller;

import br.labprog.fluxarte.dto.request.HistoricoVisualizacaoRequest;
import br.labprog.fluxarte.dto.response.HistoricoVisualizacaoResponse;
import br.labprog.fluxarte.security.JwtService;
import br.labprog.fluxarte.service.HistoricoVisualizacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HistoricoVisualizacaoController.class)
class HistoricoVisualizacaoControllerTest {

    private static final String URL = "/api/historico";

    private static final UUID USUARIO_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private HistoricoVisualizacaoService historicoService;

    // O JwtFilter faz parte da fatia web e exige um JwtService no construtor.
    @MockitoBean
    private JwtService jwtService;

    // ======================================================================
    // POST /api/historico  (registrar)
    // O usuário vem do @AuthenticationPrincipal (UUID), nunca do corpo.
    // ======================================================================

    @Test
    void registrar_comDadosValidos_deveRetornar201ComHistoricoCriado() throws Exception {
        HistoricoVisualizacaoRequest request = historicoRequestPadrao();
        HistoricoVisualizacaoResponse response = historicoResponse(1L, request);
        when(historicoService.registrar(USUARIO_ID, request)).thenReturn(response);

        mockMvc.perform(post(URL)
                        .with(usuario())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(historicoService).registrar(USUARIO_ID, request);
        verifyNoMoreInteractions(historicoService);
    }

    @Test
    void registrar_comJsonMalformado_deveRetornar400() throws Exception {
        mockMvc.perform(post(URL)
                        .with(usuario())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ isso nao e json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(historicoService);
    }

    @Test
    void registrar_comCorpoInvalido_deveRetornar400() throws Exception {
        // "{}" viola a validação (@Valid) desde que HistoricoVisualizacaoRequest tenha algum campo obrigatório.
        mockMvc.perform(post(URL)
                        .with(usuario())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(historicoService);
    }

    // ======================================================================
    // GET /api/historico  (listarMeuHistorico)
    // ======================================================================

    @Test
    void listarMeuHistorico_comRegistros_deveRetornar200ComListaDoUsuario() throws Exception {
        List<HistoricoVisualizacaoResponse> historico = List.of(
                historicoResponse(1L, historicoRequestPadrao()),
                historicoResponse(2L, historicoRequestPadrao()));
        when(historicoService.listarPorUsuario(USUARIO_ID)).thenReturn(historico);

        mockMvc.perform(get(URL)
                        .with(usuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(objectMapper.writeValueAsString(historico)));

        verify(historicoService).listarPorUsuario(USUARIO_ID);
        verifyNoMoreInteractions(historicoService);
    }

    @Test
    void listarMeuHistorico_semRegistros_deveRetornar200ComListaVazia() throws Exception {
        when(historicoService.listarPorUsuario(USUARIO_ID)).thenReturn(List.of());

        mockMvc.perform(get(URL)
                        .with(usuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(historicoService).listarPorUsuario(USUARIO_ID);
        verifyNoMoreInteractions(historicoService);
    }

    // ======================================================================
    // Auxiliares
    // ======================================================================

    /**
     * Usuário autenticado cujo principal é o UUID esperado por @AuthenticationPrincipal.
     * Com user("...") o principal seria um UserDetails e o controller receberia null.
     */
    private static RequestPostProcessor usuario() {
        return authentication(new UsernamePasswordAuthenticationToken(
                USUARIO_ID, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))));
    }

    // ---- Únicos pontos que dependem dos DTOs: preencher com os campos reais ----

    private HistoricoVisualizacaoRequest historicoRequestPadrao() {
        return new HistoricoVisualizacaoRequest(
                1L,
                120,
                false
        );
    }

    /** Response derivado do Request: campos equivalentes iguais, mais id e demais campos só do Response. */
    private HistoricoVisualizacaoResponse historicoResponse(
            Long id,
            HistoricoVisualizacaoRequest request
    ) {
        return new HistoricoVisualizacaoResponse(
                id,
                10L,
                "Toy Story",
                "https://exemplo.com/poster.png",
                request.midiaId(),
                LocalDateTime.of(2026, 10, 13, 12, 0),
                request.tempoAssistidoSegundos(),
                request.concluido()
        );
    }
}