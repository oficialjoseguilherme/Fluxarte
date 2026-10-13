package br.labprog.fluxarte.controller;

import br.labprog.fluxarte.dto.request.EventoRequest;
import br.labprog.fluxarte.dto.response.EventoResponse;
import br.labprog.fluxarte.exceptions.RecursoNaoEncontradoException;
import br.labprog.fluxarte.security.JwtService;
import br.labprog.fluxarte.service.EventoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDate;
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

@WebMvcTest(EventoController.class)
class EventoControllerTest {

    private static final String URL = "/api/eventos";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventoService eventoService;

    // O JwtFilter faz parte da fatia web e exige um JwtService no construtor.
    @MockitoBean
    private JwtService jwtService;

    // ======================================================================
    // POST /api/eventos  (cadastrar)
    // ======================================================================

    @Test
    void cadastrar_comDadosValidos_deveRetornar201ComEventoCriado() throws Exception {
        EventoRequest request = eventoRequestPadrao();
        when(eventoService.cadastrar(request)).thenReturn(eventoResponse(1L, request.nome()));

        mockMvc.perform(post(URL)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value(request.nome()));
    }

    @Test
    void cadastrar_comJsonMalformado_deveRetornar400() throws Exception {
        mockMvc.perform(post(URL)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ isso nao e json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(eventoService);
    }

    @Test
    void cadastrar_comCorpoInvalido_deveRetornar400() throws Exception {
        // "{}" viola a validação (@Valid) desde que EventoRequest tenha algum campo obrigatório.
        mockMvc.perform(post(URL)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(eventoService);
    }

    // ======================================================================
    // PUT /api/eventos/{id}  (atualizar)
    // ======================================================================

    @Test
    void atualizar_comIdValido_deveRetornar200ComEventoAtualizado() throws Exception {
        EventoRequest request = eventoRequestPadrao();
        when(eventoService.atualizar(1L, request)).thenReturn(eventoResponse(1L, request.nome()));

        mockMvc.perform(put(URL + "/{id}", 1L)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value(request.nome()));
    }

    @Test
    void atualizar_comIdInexistente_deveRetornar404() throws Exception {
        EventoRequest request = eventoRequestPadrao();
        when(eventoService.atualizar(99L, request))
                .thenThrow(new RecursoNaoEncontradoException("Evento não encontrado."));

        mockMvc.perform(put(URL + "/{id}", 99L)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Evento não encontrado."));
    }

    @Test
    void atualizar_comIdNaoNumerico_deveRetornar400() throws Exception {
        mockMvc.perform(put(URL + "/{id}", "abc")
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventoRequestPadrao())))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(eventoService);
    }

    @Test
    void atualizar_comCorpoInvalido_deveRetornar400() throws Exception {
        mockMvc.perform(put(URL + "/{id}", 1L)
                        .with(admin())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(eventoService);
    }

    // ======================================================================
    // GET /api/eventos/{id}  (buscarPorId)
    // ======================================================================

    @Test
    void buscarPorId_comIdExistente_deveRetornar200ComEvento() throws Exception {
        when(eventoService.buscarPorId(1L)).thenReturn(eventoResponse(1L, "Festival de Cinema"));

        mockMvc.perform(get(URL + "/{id}", 1L)
                        .with(usuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Festival de Cinema"));
    }

    @Test
    void buscarPorId_comIdInexistente_deveRetornar404() throws Exception {
        when(eventoService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Evento não encontrado."));

        mockMvc.perform(get(URL + "/{id}", 99L)
                        .with(usuario()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Evento não encontrado."));
    }

    @Test
    void buscarPorId_comIdNaoNumerico_deveRetornar400() throws Exception {
        mockMvc.perform(get(URL + "/{id}", "abc")
                        .with(usuario()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(eventoService);
    }

    // ======================================================================
    // GET /api/eventos  (listar)
    // Três caminhos, nesta ordem de prioridade:
    //   1) nome preenchido (não vazio/branco) -> buscarPorNome
    //   2) emAndamento == true                -> listarEmAndamento
    //   3) qualquer outro caso                -> listarTodos
    // ======================================================================

    @Test
    void listar_semParametros_deveChamarListarTodos() throws Exception {
        when(eventoService.listarTodos()).thenReturn(List.of(
                eventoResponse(1L, "Festival de Cinema"),
                eventoResponse(2L, "Mostra de Documentários")));

        mockMvc.perform(get(URL)
                        .with(usuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", contains(1, 2)))
                .andExpect(jsonPath("$[*].nome", contains("Festival de Cinema", "Mostra de Documentários")));

        verify(eventoService).listarTodos();
        verifyNoMoreInteractions(eventoService);
    }

    @Test
    void listar_semResultados_deveRetornar200ComListaVazia() throws Exception {
        when(eventoService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get(URL)
                        .with(usuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void listar_comNome_deveChamarBuscarPorNome() throws Exception {
        when(eventoService.buscarPorNome("festival"))
                .thenReturn(List.of(eventoResponse(1L, "Festival de Cinema")));

        mockMvc.perform(get(URL)
                        .param("nome", "festival")
                        .with(usuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Festival de Cinema"));

        verify(eventoService).buscarPorNome("festival");
        verifyNoMoreInteractions(eventoService);
    }

    @Test
    void listar_comEmAndamentoTrue_deveChamarListarEmAndamento() throws Exception {
        when(eventoService.listarEmAndamento())
                .thenReturn(List.of(eventoResponse(3L, "Semana de Cinema Nacional")));

        mockMvc.perform(get(URL)
                        .param("emAndamento", "true")
                        .with(usuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(3));

        verify(eventoService).listarEmAndamento();
        verifyNoMoreInteractions(eventoService);
    }

    @Test
    void listar_comEmAndamentoFalse_deveChamarListarTodos() throws Exception {
        when(eventoService.listarTodos()).thenReturn(List.of(eventoResponse(1L, "Festival de Cinema")));

        mockMvc.perform(get(URL)
                        .param("emAndamento", "false")
                        .with(usuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(eventoService).listarTodos();
        verifyNoMoreInteractions(eventoService);
    }

    @Test
    void listar_comNomeEEmAndamento_nomeTemPrioridade() throws Exception {
        when(eventoService.buscarPorNome("festival"))
                .thenReturn(List.of(eventoResponse(1L, "Festival de Cinema")));

        mockMvc.perform(get(URL)
                        .param("nome", "festival")
                        .param("emAndamento", "true")
                        .with(usuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(eventoService).buscarPorNome("festival");
        verifyNoMoreInteractions(eventoService);
    }

    @Test
    void listar_comNomeEmBranco_deveChamarListarTodos() throws Exception {
        when(eventoService.listarTodos()).thenReturn(List.of(eventoResponse(1L, "Festival de Cinema")));

        mockMvc.perform(get(URL)
                        .param("nome", "   ")
                        .with(usuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(eventoService).listarTodos();
        verifyNoMoreInteractions(eventoService);
    }

    @Test
    void listar_comNomeEmBrancoEEmAndamentoTrue_deveChamarListarEmAndamento() throws Exception {
        when(eventoService.listarEmAndamento())
                .thenReturn(List.of(eventoResponse(3L, "Semana de Cinema Nacional")));

        mockMvc.perform(get(URL)
                        .param("nome", "   ")
                        .param("emAndamento", "true")
                        .with(usuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3));

        verify(eventoService).listarEmAndamento();
        verifyNoMoreInteractions(eventoService);
    }

    @Test
    void listar_comEmAndamentoNaoBooleano_deveRetornar400() throws Exception {
        mockMvc.perform(get(URL)
                        .param("emAndamento", "talvez")
                        .with(usuario()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(eventoService);
    }

    // ======================================================================
    // DELETE /api/eventos/{id}  (excluir)
    // ======================================================================

    @Test
    void excluir_comIdValido_deveRetornar204SemCorpo() throws Exception {
        mockMvc.perform(delete(URL + "/{id}", 1L)
                        .with(admin())
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(eventoService).excluir(1L);
    }

    @Test
    void excluir_comIdInexistente_deveRetornar404() throws Exception {
        doThrow(new RecursoNaoEncontradoException("Evento não encontrado."))
                .when(eventoService).excluir(99L);

        mockMvc.perform(delete(URL + "/{id}", 99L)
                        .with(admin())
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Evento não encontrado."));
    }

    @Test
    void excluir_comIdNaoNumerico_deveRetornar400() throws Exception {
        mockMvc.perform(delete(URL + "/{id}", "abc")
                        .with(admin())
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(eventoService);
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

    // ---- Únicos pontos que dependem dos DTOs: preencher com os campos reais ----

    private EventoRequest eventoRequestPadrao() {
        return new EventoRequest(
                "Semana de Computação",
                "Evento acadêmico de computação.",
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 12),
                "https://exemplo.com/banner.png"
        );
    }

    private EventoResponse eventoResponse(Long id, String nome) {
        return new EventoResponse(
                id,
                nome,
                "Evento acadêmico de computação.",
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 12),
                "https://exemplo.com/banner.png",
                false
        );
    }
}