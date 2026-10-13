package br.labprog.fluxarte.controller;

import br.labprog.fluxarte.dto.request.AtividadeUsuarioRequest;
import br.labprog.fluxarte.dto.response.AtividadeUsuarioResponse;
import br.labprog.fluxarte.model.enums.TipoAtividade;
import br.labprog.fluxarte.security.JwtService;
import br.labprog.fluxarte.service.AtividadeUsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AtividadeUsuarioController.class)
class AtividadeUsuarioControllerTest {

    private static final String URL = "/api/atividades";

    private static final UUID USUARIO_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final Long OBRA_ID = 10L;
    private static final String TERMO_BUSCA = "documentário";
    private static final String METADADOS_JSON = "{\"origem\":\"home\"}";

    // Único valor de TipoAtividade conhecido é BUSCA; para os demais usa-se a primeira constante diferente dele.
    private static final TipoAtividade TIPO_NAO_BUSCA = Arrays.stream(TipoAtividade.values())
            .filter(tipo -> tipo != TipoAtividade.BUSCA)
            .findFirst()
            .orElseThrow();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AtividadeUsuarioService atividadeService;

    // O JwtFilter faz parte da fatia web e exige um JwtService no construtor.
    @MockitoBean
    private JwtService jwtService;

    // ======================================================================
    // POST /api/atividades  (registrar)
    // BUSCA exige termoBusca; as demais atividades exigem obraIdReferenciada.
    // ======================================================================

    @Test
    void registrar_comAtividadeDeBuscaValida_deveRetornar201ComAtividadeCriada() throws Exception {
        AtividadeUsuarioRequest request = atividadeRequestBuscaPadrao();
        when(atividadeService.registrar(USUARIO_ID, request)).thenReturn(atividadeResponse(1L, request));

        mockMvc.perform(post(URL)
                        .with(usuario())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipoAtividade").value("BUSCA"))
                .andExpect(jsonPath("$.termoBusca").value(request.termoBusca()))
                .andExpect(jsonPath("$.metadadosJson").value(request.metadadosJson()));

        verify(atividadeService).registrar(USUARIO_ID, request);
        verifyNoMoreInteractions(atividadeService);
    }

    @Test
    void registrar_comAtividadeComObraValida_deveRetornar201ComAtividadeCriada() throws Exception {
        AtividadeUsuarioRequest request = atividadeRequestComObraPadrao();
        when(atividadeService.registrar(USUARIO_ID, request)).thenReturn(atividadeResponse(2L, request));

        mockMvc.perform(post(URL)
                        .with(usuario())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.tipoAtividade").value(TIPO_NAO_BUSCA.name()))
                .andExpect(jsonPath("$.obraIdReferenciada").value(10))
                .andExpect(jsonPath("$.metadadosJson").value(request.metadadosJson()));

        verify(atividadeService).registrar(USUARIO_ID, request);
        verifyNoMoreInteractions(atividadeService);
    }

    @Test
    void registrar_comJsonMalformado_deveRetornar400() throws Exception {
        mockMvc.perform(post(URL)
                        .with(usuario())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ isso nao e json"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(atividadeService);
    }

    @Test
    void registrar_comCorpoVazio_deveRetornar400() throws Exception {
        // "{}" viola @NotNull em tipoAtividade.
        mockMvc.perform(post(URL)
                        .with(usuario())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(atividadeService);
    }

    @Test
    void registrar_comTipoAtividadeInexistente_deveRetornar400() throws Exception {
        ObjectNode json = objectMapper.valueToTree(atividadeRequestBuscaPadrao());
        json.put("tipoAtividade", "INEXISTENTE");

        mockMvc.perform(post(URL)
                        .with(usuario())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(json)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(atividadeService);
    }

    @Test
    void registrar_comBuscaSemTermo_deveRetornar400() throws Exception {
        AtividadeUsuarioRequest request = atividadeRequest(TipoAtividade.BUSCA, null, OBRA_ID, METADADOS_JSON);

        mockMvc.perform(post(URL)
                        .with(usuario())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(atividadeService);
    }

    @Test
    void registrar_comBuscaComTermoEmBranco_deveRetornar400() throws Exception {
        AtividadeUsuarioRequest request = atividadeRequest(TipoAtividade.BUSCA, "   ", null, METADADOS_JSON);

        mockMvc.perform(post(URL)
                        .with(usuario())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(atividadeService);
    }

    @Test
    void registrar_comAtividadeNaoBuscaSemObra_deveRetornar400() throws Exception {
        AtividadeUsuarioRequest request = atividadeRequest(TIPO_NAO_BUSCA, TERMO_BUSCA, null, METADADOS_JSON);

        mockMvc.perform(post(URL)
                        .with(usuario())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(atividadeService);
    }

    // ======================================================================
    // GET /api/atividades  (listarMinhasAtividades)
    // Dois caminhos:
    //   1) tipo informado -> listarPorUsuarioETipo
    //   2) tipo ausente   -> listarPorUsuario
    // ======================================================================

    @Test
    void listarMinhasAtividades_semTipo_deveChamarListarPorUsuario() throws Exception {
        when(atividadeService.listarPorUsuario(USUARIO_ID)).thenReturn(List.of(
                atividadeResponse(1L, TipoAtividade.BUSCA, TERMO_BUSCA, null),
                atividadeResponse(2L, TIPO_NAO_BUSCA, null, OBRA_ID)));

        mockMvc.perform(get(URL)
                        .with(usuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", contains(1, 2)))
                .andExpect(jsonPath("$[*].tipoAtividade", contains("BUSCA", TIPO_NAO_BUSCA.name())));

        verify(atividadeService).listarPorUsuario(USUARIO_ID);
        verifyNoMoreInteractions(atividadeService);
    }

    @Test
    void listarMinhasAtividades_semResultados_deveRetornar200ComListaVazia() throws Exception {
        when(atividadeService.listarPorUsuario(USUARIO_ID)).thenReturn(List.of());

        mockMvc.perform(get(URL)
                        .with(usuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(atividadeService).listarPorUsuario(USUARIO_ID);
        verifyNoMoreInteractions(atividadeService);
    }

    @Test
    void listarMinhasAtividades_comTipo_deveChamarListarPorUsuarioETipo() throws Exception {
        when(atividadeService.listarPorUsuarioETipo(USUARIO_ID, TipoAtividade.BUSCA))
                .thenReturn(List.of(atividadeResponse(1L, TipoAtividade.BUSCA, TERMO_BUSCA, null)));

        mockMvc.perform(get(URL)
                        .param("tipo", TipoAtividade.BUSCA.name())
                        .with(usuario()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].tipoAtividade").value("BUSCA"))
                .andExpect(jsonPath("$[0].termoBusca").value(TERMO_BUSCA));

        verify(atividadeService).listarPorUsuarioETipo(USUARIO_ID, TipoAtividade.BUSCA);
        verifyNoMoreInteractions(atividadeService);
    }

    @Test
    void listarMinhasAtividades_comTipoInexistente_deveRetornar400() throws Exception {
        mockMvc.perform(get(URL)
                        .param("tipo", "INEXISTENTE")
                        .with(usuario()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(atividadeService);
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

    private AtividadeUsuarioRequest atividadeRequest(TipoAtividade tipoAtividade,
                                                     String termoBusca,
                                                     Long obraIdReferenciada,
                                                     String metadadosJson) {
        return new AtividadeUsuarioRequest(tipoAtividade, termoBusca, obraIdReferenciada, metadadosJson);
    }

    /** Request válido de busca: exige o termo pesquisado. */
    private AtividadeUsuarioRequest atividadeRequestBuscaPadrao() {
        return atividadeRequest(TipoAtividade.BUSCA, TERMO_BUSCA, null, METADADOS_JSON);
    }

    /** Request válido de atividade que não é busca: exige a obra referenciada. */
    private AtividadeUsuarioRequest atividadeRequestComObraPadrao() {
        return atividadeRequest(TIPO_NAO_BUSCA, null, OBRA_ID, METADADOS_JSON);
    }

    /** Response coerente com o request: todos os campos equivalentes copiados, apenas o id é adicional. */
    private AtividadeUsuarioResponse atividadeResponse(Long id, AtividadeUsuarioRequest request) {
        return atividadeResponse(id, request.tipoAtividade(), request.termoBusca(), request.obraIdReferenciada(),
                request.metadadosJson());
    }

    /** Response para cenários de leitura, sem request associado. */
    private AtividadeUsuarioResponse atividadeResponse(Long id,
                                                       TipoAtividade tipoAtividade,
                                                       String termoBusca,
                                                       Long obraIdReferenciada) {
        return atividadeResponse(id, tipoAtividade, termoBusca, obraIdReferenciada, METADADOS_JSON);
    }

    private AtividadeUsuarioResponse atividadeResponse(Long id,
                                                       TipoAtividade tipoAtividade,
                                                       String termoBusca,
                                                       Long obraIdReferenciada,
                                                       String metadadosJson) {
        return new AtividadeUsuarioResponse(id, tipoAtividade, termoBusca, obraIdReferenciada, metadadosJson);
    }
}