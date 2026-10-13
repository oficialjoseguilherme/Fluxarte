package br.labprog.fluxarte.controller;

import br.labprog.fluxarte.dto.request.EventoObraRequest;
import br.labprog.fluxarte.dto.response.EventoObraResponse;
import br.labprog.fluxarte.exceptions.RecursoNaoEncontradoException;
import br.labprog.fluxarte.model.enums.CategoriaEvento;
import br.labprog.fluxarte.security.JwtService;
import br.labprog.fluxarte.service.EventoObraService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

@WebMvcTest(EventoObraController.class)
class EventoObraControllerTest {

    private static final String URL = "/api/eventos/{eventoId}/obras";
    private static final String URL_ID = URL + "/{id}";

    private static final Long EVENTO_ID = 1L;
    private static final Long OBRA_ID = 10L;
    private static final Long ID = 100L;

    // Não conheço os valores do enum; uso o primeiro para não inventar nenhum.
    private static final CategoriaEvento CATEGORIA = CategoriaEvento.values()[0];

    private static final String TITULO_OBRA = "Obra de Teste";
    private static final String POSTER_URL = "https://exemplo.com/poster.jpg";
    private static final String PREMIACAO = "Melhor Curta-Metragem";
    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 12, 1, 19, 0);
    private static final LocalDateTime FIM = LocalDateTime.of(2026, 12, 1, 21, 0);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventoObraService eventoObraService;

    // O JwtFilter faz parte da fatia web e exige um JwtService no construtor.
    @MockitoBean
    private JwtService jwtService;

    // ======================================================================
    // POST /api/eventos/{eventoId}/obras  (adicionarObra)
    // ======================================================================

    @Test
    void adicionarObra_comDadosValidos_deveRetornar201ComObraCriada() throws Exception {
        EventoObraRequest request = eventoObraRequestPadrao();
        when(eventoObraService.adicionarObra(EVENTO_ID, request))
                .thenReturn(eventoObraResponse(ID, request));

        mockMvc.perform(post(URL, EVENTO_ID)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.obraId").value(OBRA_ID))
                .andExpect(jsonPath("$.tituloObra").value(TITULO_OBRA))
                .andExpect(jsonPath("$.posterUrlObra").value(POSTER_URL))
                .andExpect(jsonPath("$.categoria").value(CATEGORIA.name()))
                .andExpect(jsonPath("$.dataEstreiaEventoInicio").value(iso(INICIO)))
                .andExpect(jsonPath("$.dataEstreiaEventoFim").value(iso(FIM)))
                .andExpect(jsonPath("$.premiacao").value(PREMIACAO))
                .andExpect(jsonPath("$.destaque").value(true));

        verify(eventoObraService).adicionarObra(EVENTO_ID, request);
        verifyNoMoreInteractions(eventoObraService);
    }

    @Test
    void adicionarObra_somenteComCamposObrigatorios_deveAssumirDestaqueFalse() throws Exception {
        String json = String.format("{\"obraId\":%d,\"categoria\":\"%s\"}", OBRA_ID, CATEGORIA.name());
        // O construtor compacto do record converte destaque null em false.
        EventoObraRequest esperado = new EventoObraRequest(OBRA_ID, CATEGORIA, null, null, null, null);
        when(eventoObraService.adicionarObra(EVENTO_ID, esperado))
                .thenReturn(eventoObraResponse(ID, esperado));

        mockMvc.perform(post(URL, EVENTO_ID)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.obraId").value(OBRA_ID))
                .andExpect(jsonPath("$.destaque").value(false));

        verify(eventoObraService).adicionarObra(EVENTO_ID, esperado);
        verifyNoMoreInteractions(eventoObraService);
    }

    @Test
    void adicionarObra_comObraIdNulo_deveRetornar400() throws Exception {
        EventoObraRequest request = new EventoObraRequest(null, CATEGORIA, INICIO, FIM, PREMIACAO, true);

        mockMvc.perform(post(URL, EVENTO_ID)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(eventoObraService);
    }

    @Test
    void adicionarObra_comCategoriaNula_deveRetornar400() throws Exception {
        EventoObraRequest request = new EventoObraRequest(OBRA_ID, null, INICIO, FIM, PREMIACAO, true);

        mockMvc.perform(post(URL, EVENTO_ID)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(eventoObraService);
    }

    @Test
    void adicionarObra_comPremiacaoMaiorQue80Caracteres_deveRetornar400() throws Exception {
        EventoObraRequest request =
                new EventoObraRequest(OBRA_ID, CATEGORIA, INICIO, FIM, "x".repeat(81), true);

        mockMvc.perform(post(URL, EVENTO_ID)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(eventoObraService);
    }

    @Test
    void adicionarObra_comFimAntesDoInicio_deveRetornar400() throws Exception {
        EventoObraRequest request =
                new EventoObraRequest(OBRA_ID, CATEGORIA, FIM, INICIO, PREMIACAO, true);

        mockMvc.perform(post(URL, EVENTO_ID)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(eventoObraService);
    }

    @Test
    void adicionarObra_comApenasDataDeInicio_deveRetornar400() throws Exception {
        EventoObraRequest request =
                new EventoObraRequest(OBRA_ID, CATEGORIA, INICIO, null, PREMIACAO, true);

        mockMvc.perform(post(URL, EVENTO_ID)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(eventoObraService);
    }

    @Test
    void adicionarObra_comJsonMalformado_deveRetornar400() throws Exception {
        mockMvc.perform(post(URL, EVENTO_ID)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ isso nao e json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(eventoObraService);
    }

    @Test
    void adicionarObra_comEventoOuObraInexistente_deveRetornar404() throws Exception {
        EventoObraRequest request = eventoObraRequestPadrao();
        when(eventoObraService.adicionarObra(EVENTO_ID, request))
                .thenThrow(new RecursoNaoEncontradoException("Evento não encontrado."));

        mockMvc.perform(post(URL, EVENTO_ID)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Evento não encontrado."));
    }

    @Test
    void adicionarObra_comEventoIdNaoNumerico_deveRetornar400() throws Exception {
        mockMvc.perform(post(URL, "abc")
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventoObraRequestPadrao())))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(eventoObraService);
    }

    // ======================================================================
    // GET /api/eventos/{eventoId}/obras  (listarObrasDoEvento)
    // Dois caminhos:
    //   1) destaque == true  -> listarDestaquesDoEvento
    //   2) qualquer outro    -> listarPorEvento
    // ======================================================================

    @Test
    void listarObrasDoEvento_semParametros_deveChamarListarPorEvento() throws Exception {
        when(eventoObraService.listarPorEvento(EVENTO_ID)).thenReturn(List.of(
                eventoObraResponse(100L, eventoObraRequest(10L, true)),
                eventoObraResponse(101L, eventoObraRequest(11L, false))));

        mockMvc.perform(get(URL, EVENTO_ID)
                        .with(usuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", contains(100, 101)))
                .andExpect(jsonPath("$[*].obraId", contains(10, 11)))
                .andExpect(jsonPath("$[*].destaque", contains(true, false)));

        verify(eventoObraService).listarPorEvento(EVENTO_ID);
        verifyNoMoreInteractions(eventoObraService);
    }

    @Test
    void listarObrasDoEvento_semResultados_deveRetornar200ComListaVazia() throws Exception {
        when(eventoObraService.listarPorEvento(EVENTO_ID)).thenReturn(List.of());

        mockMvc.perform(get(URL, EVENTO_ID)
                        .with(usuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void listarObrasDoEvento_comDestaqueTrue_deveChamarListarDestaquesDoEvento() throws Exception {
        when(eventoObraService.listarDestaquesDoEvento(EVENTO_ID))
                .thenReturn(List.of(eventoObraResponse(100L, eventoObraRequest(10L, true))));

        mockMvc.perform(get(URL, EVENTO_ID)
                        .param("destaque", "true")
                        .with(usuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].destaque").value(true));

        verify(eventoObraService).listarDestaquesDoEvento(EVENTO_ID);
        verifyNoMoreInteractions(eventoObraService);
    }

    @Test
    void listarObrasDoEvento_comDestaqueFalse_deveChamarListarPorEvento() throws Exception {
        when(eventoObraService.listarPorEvento(EVENTO_ID)).thenReturn(List.of(
                eventoObraResponse(100L, eventoObraRequest(10L, true)),
                eventoObraResponse(101L, eventoObraRequest(11L, false))));

        mockMvc.perform(get(URL, EVENTO_ID)
                        .param("destaque", "false")
                        .with(usuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(eventoObraService).listarPorEvento(EVENTO_ID);
        verifyNoMoreInteractions(eventoObraService);
    }

    @Test
    void listarObrasDoEvento_comEventoInexistente_deveRetornar404() throws Exception {
        when(eventoObraService.listarPorEvento(99L))
                .thenThrow(new RecursoNaoEncontradoException("Evento não encontrado."));

        mockMvc.perform(get(URL, 99L)
                        .with(usuario()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Evento não encontrado."));
    }

    @Test
    void listarObrasDoEvento_comDestaqueNaoBooleano_deveRetornar400() throws Exception {
        mockMvc.perform(get(URL, EVENTO_ID)
                        .param("destaque", "talvez")
                        .with(usuario()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(eventoObraService);
    }

    @Test
    void listarObrasDoEvento_comEventoIdNaoNumerico_deveRetornar400() throws Exception {
        mockMvc.perform(get(URL, "abc")
                        .with(usuario()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(eventoObraService);
    }

    // ======================================================================
    // GET /api/eventos/{eventoId}/obras/{id}  (buscarPorId)
    // O controller repassa somente o id ao service; o eventoId é ignorado.
    // ======================================================================

    @Test
    void buscarPorId_comIdExistente_deveRetornar200ComObraDoEvento() throws Exception {
        when(eventoObraService.buscarPorId(ID))
                .thenReturn(eventoObraResponse(ID, eventoObraRequestPadrao()));

        mockMvc.perform(get(URL_ID, EVENTO_ID, ID)
                        .with(usuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.obraId").value(OBRA_ID))
                .andExpect(jsonPath("$.tituloObra").value(TITULO_OBRA))
                .andExpect(jsonPath("$.categoria").value(CATEGORIA.name()))
                .andExpect(jsonPath("$.premiacao").value(PREMIACAO))
                .andExpect(jsonPath("$.destaque").value(true));

        verify(eventoObraService).buscarPorId(ID);
        verifyNoMoreInteractions(eventoObraService);
    }

    @Test
    void buscarPorId_comIdInexistente_deveRetornar404() throws Exception {
        when(eventoObraService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Obra do evento não encontrada."));

        mockMvc.perform(get(URL_ID, EVENTO_ID, 99L)
                        .with(usuario()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Obra do evento não encontrada."));
    }

    @Test
    void buscarPorId_comIdNaoNumerico_deveRetornar400() throws Exception {
        mockMvc.perform(get(URL_ID, EVENTO_ID, "abc")
                        .with(usuario()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(eventoObraService);
    }

    // ======================================================================
    // PUT /api/eventos/{eventoId}/obras/{id}  (atualizar)
    // ======================================================================

    @Test
    void atualizar_comDadosValidos_deveRetornar200ComObraAtualizada() throws Exception {
        EventoObraRequest request = new EventoObraRequest(
                OBRA_ID, CATEGORIA, INICIO.plusDays(1), FIM.plusDays(1), "Prêmio do Público", false);
        when(eventoObraService.atualizar(ID, request))
                .thenReturn(eventoObraResponse(ID, request));

        mockMvc.perform(put(URL_ID, EVENTO_ID, ID)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.obraId").value(OBRA_ID))
                .andExpect(jsonPath("$.categoria").value(CATEGORIA.name()))
                .andExpect(jsonPath("$.dataEstreiaEventoInicio").value(iso(INICIO.plusDays(1))))
                .andExpect(jsonPath("$.dataEstreiaEventoFim").value(iso(FIM.plusDays(1))))
                .andExpect(jsonPath("$.premiacao").value("Prêmio do Público"))
                .andExpect(jsonPath("$.destaque").value(false));

        verify(eventoObraService).atualizar(ID, request);
        verifyNoMoreInteractions(eventoObraService);
    }

    @Test
    void atualizar_comIdInexistente_deveRetornar404() throws Exception {
        EventoObraRequest request = eventoObraRequestPadrao();
        when(eventoObraService.atualizar(99L, request))
                .thenThrow(new RecursoNaoEncontradoException("Obra do evento não encontrada."));

        mockMvc.perform(put(URL_ID, EVENTO_ID, 99L)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Obra do evento não encontrada."));
    }

    @Test
    void atualizar_comIdNaoNumerico_deveRetornar400() throws Exception {
        mockMvc.perform(put(URL_ID, EVENTO_ID, "abc")
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventoObraRequestPadrao())))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(eventoObraService);
    }

    @Test
    void atualizar_comCorpoInvalido_deveRetornar400() throws Exception {
        mockMvc.perform(put(URL_ID, EVENTO_ID, ID)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(eventoObraService);
    }

    @Test
    void atualizar_comFimAntesDoInicio_deveRetornar400() throws Exception {
        EventoObraRequest request =
                new EventoObraRequest(OBRA_ID, CATEGORIA, FIM, INICIO, PREMIACAO, true);

        mockMvc.perform(put(URL_ID, EVENTO_ID, ID)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(eventoObraService);
    }

    // ======================================================================
    // DELETE /api/eventos/{eventoId}/obras/{id}  (remover)
    // ======================================================================

    @Test
    void remover_comIdValido_deveRetornar204SemCorpo() throws Exception {
        mockMvc.perform(delete(URL_ID, EVENTO_ID, ID)
                        .with(admin())
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(eventoObraService).remover(ID);
        verifyNoMoreInteractions(eventoObraService);
    }

    @Test
    void remover_comIdInexistente_deveRetornar404() throws Exception {
        doThrow(new RecursoNaoEncontradoException("Obra do evento não encontrada."))
                .when(eventoObraService).remover(99L);

        mockMvc.perform(delete(URL_ID, EVENTO_ID, 99L)
                        .with(admin())
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Obra do evento não encontrada."));
    }

    @Test
    void remover_comIdNaoNumerico_deveRetornar400() throws Exception {
        mockMvc.perform(delete(URL_ID, EVENTO_ID, "abc")
                        .with(admin())
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(eventoObraService);
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

    private EventoObraRequest eventoObraRequestPadrao() {
        return eventoObraRequest(OBRA_ID, true);
    }

    private EventoObraRequest eventoObraRequest(Long obraId, boolean destaque) {
        return new EventoObraRequest(obraId, CATEGORIA, INICIO, FIM, PREMIACAO, destaque);
    }

    /** Response derivado do Request; só id, tituloObra e posterUrlObra existem apenas no Response. */
    private EventoObraResponse eventoObraResponse(Long id, EventoObraRequest request) {
        return new EventoObraResponse(
                id,
                request.obraId(),
                TITULO_OBRA,
                POSTER_URL,
                request.categoria(),
                request.dataEstreiaEventoInicio(),
                request.dataEstreiaEventoFim(),
                request.premiacao(),
                request.destaque()
        );
    }

    private static String iso(LocalDateTime dateTime) {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dateTime);
    }
}