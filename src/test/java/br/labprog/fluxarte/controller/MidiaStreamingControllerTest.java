package br.labprog.fluxarte.controller;

import br.labprog.fluxarte.dto.request.MidiaStreamingRequest;
import br.labprog.fluxarte.dto.response.MidiaStreamingResponse;
import br.labprog.fluxarte.exceptions.RecursoNaoEncontradoException;
import br.labprog.fluxarte.model.enums.FormatoMidia;
import br.labprog.fluxarte.model.enums.Resolucao;
import br.labprog.fluxarte.model.enums.TipoMidia;
import br.labprog.fluxarte.model.enums.TipoPlayer;
import br.labprog.fluxarte.security.JwtService;
import br.labprog.fluxarte.service.MidiaStreamingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MidiaStreamingController.class)
class MidiaStreamingControllerTest {

    private static final String URL = "/api/midias";

    private static final Long OBRA_ID = 10L;
    private static final Integer DURACAO_SEGUNDOS = 3600;
    private static final String EDUPLAY_URL = "https://eduplay.exemplo.com/embed/123";
    private static final String CDN_URL = "https://cdn.exemplo.com/stream/123.m3u8";
    private static final String THUMBS_URL = "https://cdn.exemplo.com/thumbs/123.jpg";

    // Os valores reais dos enums não foram informados; usa-se a primeira constante de cada um.
    private static final TipoMidia TIPO_MIDIA = TipoMidia.values()[0];
    private static final Resolucao RESOLUCAO = Resolucao.values()[0];
    private static final FormatoMidia FORMATO = FormatoMidia.values()[0];

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MidiaStreamingService midiaService;

    // O JwtFilter faz parte da fatia web e exige um JwtService no construtor.
    @MockitoBean
    private JwtService jwtService;

    // ======================================================================
    // POST /api/midias  (cadastrar)
    // ======================================================================

    @Test
    void cadastrar_comPlayerEmbedValido_deveRetornar201ComMidiaCriada() throws Exception {
        MidiaStreamingRequest request = midiaRequestPadrao();
        when(midiaService.cadastrar(request)).thenReturn(midiaResponse(1L, request));

        mockMvc.perform(post(URL)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.obraId").value(10))
                .andExpect(jsonPath("$.tipoMidia").value(request.tipoMidia().name()))
                .andExpect(jsonPath("$.playerType").value("EMBED"))
                .andExpect(jsonPath("$.eduplayEmbedUrl").value(request.eduplayEmbedUrl()))
                .andExpect(jsonPath("$.duracaoSegundos").value(request.duracaoSegundos()));

        verify(midiaService).cadastrar(request);
        verifyNoMoreInteractions(midiaService);
    }

    @Test
    void cadastrar_comPlayerNativeValido_deveRetornar201ComMidiaCriada() throws Exception {
        MidiaStreamingRequest request = midiaRequestNativePadrao();
        when(midiaService.cadastrar(request)).thenReturn(midiaResponse(2L, request));

        mockMvc.perform(post(URL)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.playerType").value("NATIVE"))
                .andExpect(jsonPath("$.cdnStreamUrl").value(request.cdnStreamUrl()));

        verify(midiaService).cadastrar(request);
    }

    @Test
    void cadastrar_comJsonMalformado_deveRetornar400() throws Exception {
        mockMvc.perform(post(URL)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ isso nao e json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(midiaService);
    }

    @Test
    void cadastrar_comCorpoVazio_deveRetornar400() throws Exception {
        // "{}" viola @NotNull em obraId, tipoMidia, playerType e duracaoSegundos.
        mockMvc.perform(post(URL)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(midiaService);
    }

    @Test
    void cadastrar_comTipoMidiaInexistente_deveRetornar400() throws Exception {
        ObjectNode json = objectMapper.valueToTree(midiaRequestPadrao());
        json.put("tipoMidia", "INEXISTENTE");

        mockMvc.perform(post(URL)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(json)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(midiaService);
    }

    @Test
    void cadastrar_comDuracaoZero_deveRetornar400() throws Exception {
        MidiaStreamingRequest request = midiaRequest(TipoPlayer.EMBED, EDUPLAY_URL, null, 0);

        mockMvc.perform(post(URL)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(midiaService);
    }

    @Test
    void cadastrar_comPlayerEmbedSemUrlEduplay_deveRetornar400() throws Exception {
        MidiaStreamingRequest request = midiaRequest(TipoPlayer.EMBED, null, CDN_URL, DURACAO_SEGUNDOS);

        mockMvc.perform(post(URL)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(midiaService);
    }

    @Test
    void cadastrar_comPlayerNativeComUrlCdnEmBranco_deveRetornar400() throws Exception {
        MidiaStreamingRequest request = midiaRequest(TipoPlayer.NATIVE, EDUPLAY_URL, "   ", DURACAO_SEGUNDOS);

        mockMvc.perform(post(URL)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(midiaService);
    }

    // ======================================================================
    // PUT /api/midias/{id}  (atualizar)
    // ======================================================================

    @Test
    void atualizar_comIdValido_deveRetornar200ComMidiaAtualizada() throws Exception {
        MidiaStreamingRequest request = midiaRequestNativePadrao();
        when(midiaService.atualizar(1L, request)).thenReturn(midiaResponse(1L, request));

        mockMvc.perform(put(URL + "/{id}", 1L)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.obraId").value(10))
                .andExpect(jsonPath("$.playerType").value("NATIVE"))
                .andExpect(jsonPath("$.cdnStreamUrl").value(request.cdnStreamUrl()))
                .andExpect(jsonPath("$.duracaoSegundos").value(request.duracaoSegundos()));

        verify(midiaService).atualizar(1L, request);
        verifyNoMoreInteractions(midiaService);
    }

    @Test
    void atualizar_comIdInexistente_deveRetornar404() throws Exception {
        MidiaStreamingRequest request = midiaRequestPadrao();
        when(midiaService.atualizar(99L, request))
                .thenThrow(new RecursoNaoEncontradoException("Mídia não encontrada."));

        mockMvc.perform(put(URL + "/{id}", 99L)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Mídia não encontrada."));
    }

    @Test
    void atualizar_comIdNaoNumerico_deveRetornar400() throws Exception {
        mockMvc.perform(put(URL + "/{id}", "abc")
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(midiaRequestPadrao())))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(midiaService);
    }

    @Test
    void atualizar_comCorpoInvalido_deveRetornar400() throws Exception {
        mockMvc.perform(put(URL + "/{id}", 1L)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(midiaService);
    }

    @Test
    void atualizar_comJsonMalformado_deveRetornar400() throws Exception {
        mockMvc.perform(put(URL + "/{id}", 1L)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ isso nao e json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(midiaService);
    }

    // ======================================================================
    // GET /api/midias/{id}  (buscarPorId)
    // ======================================================================

    @Test
    void buscarPorId_comIdExistente_deveRetornar200ComMidia() throws Exception {
        when(midiaService.buscarPorId(1L)).thenReturn(midiaResponse(1L, OBRA_ID, TIPO_MIDIA));

        mockMvc.perform(get(URL + "/{id}", 1L)
                        .with(usuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.obraId").value(10))
                .andExpect(jsonPath("$.tipoMidia").value(TIPO_MIDIA.name()))
                .andExpect(jsonPath("$.duracaoSegundos").value(DURACAO_SEGUNDOS));

        verify(midiaService).buscarPorId(1L);
        verifyNoMoreInteractions(midiaService);
    }

    @Test
    void buscarPorId_comIdInexistente_deveRetornar404() throws Exception {
        when(midiaService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Mídia não encontrada."));

        mockMvc.perform(get(URL + "/{id}", 99L)
                        .with(usuario()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Mídia não encontrada."));
    }

    @Test
    void buscarPorId_comIdNaoNumerico_deveRetornar400() throws Exception {
        mockMvc.perform(get(URL + "/{id}", "abc")
                        .with(usuario()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(midiaService);
    }

    // ======================================================================
    // GET /api/midias/obra/{obraId}  (listarPorObra)
    // Dois caminhos:
    //   1) tipo informado -> listarPorObraETipo
    //   2) tipo ausente   -> listarPorObra
    // ======================================================================

    @Test
    void listarPorObra_semTipo_deveChamarListarPorObra() throws Exception {
        when(midiaService.listarPorObra(OBRA_ID)).thenReturn(List.of(
                midiaResponse(1L, OBRA_ID, TIPO_MIDIA),
                midiaResponse(2L, OBRA_ID, TIPO_MIDIA)));

        mockMvc.perform(get(URL + "/obra/{obraId}", OBRA_ID)
                        .with(usuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", contains(1, 2)))
                .andExpect(jsonPath("$[*].obraId", contains(10, 10)));

        verify(midiaService).listarPorObra(OBRA_ID);
        verifyNoMoreInteractions(midiaService);
    }

    @Test
    void listarPorObra_semResultados_deveRetornar200ComListaVazia() throws Exception {
        when(midiaService.listarPorObra(OBRA_ID)).thenReturn(List.of());

        mockMvc.perform(get(URL + "/obra/{obraId}", OBRA_ID)
                        .with(usuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void listarPorObra_comTipo_deveChamarListarPorObraETipo() throws Exception {
        when(midiaService.listarPorObraETipo(OBRA_ID, TIPO_MIDIA))
                .thenReturn(List.of(midiaResponse(1L, OBRA_ID, TIPO_MIDIA)));

        mockMvc.perform(get(URL + "/obra/{obraId}", OBRA_ID)
                        .param("tipo", TIPO_MIDIA.name())
                        .with(usuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].tipoMidia").value(TIPO_MIDIA.name()));

        verify(midiaService).listarPorObraETipo(OBRA_ID, TIPO_MIDIA);
        verifyNoMoreInteractions(midiaService);
    }

    @Test
    void listarPorObra_comTipoInexistente_deveRetornar400() throws Exception {
        mockMvc.perform(get(URL + "/obra/{obraId}", OBRA_ID)
                        .param("tipo", "INEXISTENTE")
                        .with(usuario()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(midiaService);
    }

    @Test
    void listarPorObra_comObraIdNaoNumerico_deveRetornar400() throws Exception {
        mockMvc.perform(get(URL + "/obra/{obraId}", "abc")
                        .with(usuario()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(midiaService);
    }

    // ======================================================================
    // DELETE /api/midias/{id}  (excluir)
    // ======================================================================

    @Test
    void excluir_comIdValido_deveRetornar204SemCorpo() throws Exception {
        mockMvc.perform(delete(URL + "/{id}", 1L)
                        .with(admin())
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(midiaService).excluir(1L);
        verifyNoMoreInteractions(midiaService);
    }

    @Test
    void excluir_comIdInexistente_deveRetornar404() throws Exception {
        doThrow(new RecursoNaoEncontradoException("Mídia não encontrada."))
                .when(midiaService).excluir(99L);

        mockMvc.perform(delete(URL + "/{id}", 99L)
                        .with(admin())
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Mídia não encontrada."));
    }

    @Test
    void excluir_comIdNaoNumerico_deveRetornar400() throws Exception {
        mockMvc.perform(delete(URL + "/{id}", "abc")
                        .with(admin())
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(midiaService);
    }

    // ======================================================================
    // Auxiliares
    // ======================================================================

    /** Usuário com ROLE_ADMIN, para os endpoints anotados com hasRole('ADMIN'). */
    private static RequestPostProcessor admin() {
        return user("admin").roles("ADMIN");
    }

    /** Usuário autenticado comum, para os endpoints anotados com isAuthenticated(). */
    private static RequestPostProcessor usuario() {
        return user("usuario");
    }

    // ---- Únicos pontos que dependem dos DTOs ----

    private MidiaStreamingRequest midiaRequest(TipoPlayer playerType,
                                               String eduplayEmbedUrl,
                                               String cdnStreamUrl,
                                               Integer duracaoSegundos) {
        return new MidiaStreamingRequest(
                OBRA_ID,
                TIPO_MIDIA,
                playerType,
                eduplayEmbedUrl,
                cdnStreamUrl,
                RESOLUCAO,
                FORMATO,
                duracaoSegundos,
                THUMBS_URL
        );
    }

    /** Request válido com player incorporado (EMBED), que exige a URL do Eduplay. */
    private MidiaStreamingRequest midiaRequestPadrao() {
        return midiaRequest(TipoPlayer.EMBED, EDUPLAY_URL, null, DURACAO_SEGUNDOS);
    }

    /** Request válido com player nativo (NATIVE), que exige a URL de transmissão da CDN. */
    private MidiaStreamingRequest midiaRequestNativePadrao() {
        return midiaRequest(TipoPlayer.NATIVE, null, CDN_URL, DURACAO_SEGUNDOS);
    }

    /** Response coerente com o request: campos equivalentes copiados, apenas o id é adicional. */
    private MidiaStreamingResponse midiaResponse(Long id, MidiaStreamingRequest request) {
        return new MidiaStreamingResponse(
                id,
                request.obraId(),
                request.tipoMidia(),
                request.playerType(),
                request.eduplayEmbedUrl(),
                request.cdnStreamUrl(),
                request.resolucao(),
                request.formato(),
                request.duracaoSegundos(),
                request.thumbsSpriteUrl()
        );
    }

    /** Response para cenários de leitura, sem request associado. */
    private MidiaStreamingResponse midiaResponse(Long id, Long obraId, TipoMidia tipoMidia) {
        return new MidiaStreamingResponse(
                id,
                obraId,
                tipoMidia,
                TipoPlayer.EMBED,
                EDUPLAY_URL,
                null,
                RESOLUCAO,
                FORMATO,
                DURACAO_SEGUNDOS,
                THUMBS_URL
        );
    }
}