package br.labprog.fluxarte.controller;

import br.labprog.fluxarte.dto.request.AtualizarPerfilRequest;
import br.labprog.fluxarte.dto.response.UsuarioResponse;
import br.labprog.fluxarte.exceptions.ExceptionGlobalHandler;
import br.labprog.fluxarte.exceptions.RecursoNaoEncontradoException;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.model.enums.TipoUsuario;
import br.labprog.fluxarte.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private Usuario usuarioTeste;
    private UUID usuarioId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController)
                .setControllerAdvice(new ExceptionGlobalHandler())
                .build();

        usuarioId = UUID.randomUUID();
        usuarioTeste = Usuario.builder()
                .id(usuarioId)
                .nome("Maria Silva")
                .email("maria@fluxarte.com")
                .nomeExibicao("Maria")
                .dataNascimento(LocalDate.of(1995, 5, 20))
                .aceitaConteudoAdulto(true)
                .tipoUsuario(TipoUsuario.ESPECTADOR)
                .ativo(true)
                .build();
    }

    @Test
    void deveRetornarPerfilCompletoDoUsuarioAutenticado() throws Exception {
        when(usuarioService.buscarEntidadePorId(usuarioId)).thenReturn(usuarioTeste);

        var auth = new UsernamePasswordAuthenticationToken(usuarioId, null, Collections.emptyList());

        mockMvc.perform(get("/api/usuarios/me").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuarioId.toString()))
                .andExpect(jsonPath("$.nome").value("Maria Silva"))
                .andExpect(jsonPath("$.email").value("maria@fluxarte.com"))
                .andExpect(jsonPath("$.nomeExibicao").value("Maria"))
                .andExpect(jsonPath("$.tipoUsuario").value("ESPECTADOR"))
                .andExpect(jsonPath("$.ativo").value(true));

        verify(usuarioService).buscarEntidadePorId(usuarioId);
    }

    @Test
    void deveAtualizarPerfilDoUsuarioAutenticado() throws Exception {
        AtualizarPerfilRequest request = new AtualizarPerfilRequest("Maria Artista", false);
        UsuarioResponse atualizado = new UsuarioResponse(
                usuarioId,
                "Maria Silva",
                "maria@fluxarte.com",
                "Maria Artista",
                LocalDate.of(1995, 5, 20),
                false,
                TipoUsuario.ESPECTADOR,
                true
        );

        when(usuarioService.atualizarPerfil(eq(usuarioId), eq("Maria Artista"), eq(false))).thenReturn(atualizado);

        var auth = new UsernamePasswordAuthenticationToken(usuarioId, null, Collections.emptyList());

        mockMvc.perform(put("/api/usuarios/me")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuarioId.toString()))
                .andExpect(jsonPath("$.nomeExibicao").value("Maria Artista"))
                .andExpect(jsonPath("$.aceitaConteudoAdulto").value(false));

        verify(usuarioService).atualizarPerfil(usuarioId, "Maria Artista", false);
    }

    @Test
    void deveDesativarUsuarioAutenticado() throws Exception {
        doNothing().when(usuarioService).desativar(usuarioId);

        var auth = new UsernamePasswordAuthenticationToken(usuarioId, null, Collections.emptyList());

        mockMvc.perform(delete("/api/usuarios/me").principal(auth))
                .andExpect(status().isNoContent());

        verify(usuarioService).desativar(usuarioId);
    }

    @Test
    void deveBuscarUsuarioPublicoPorId() throws Exception {
        when(usuarioService.buscarEntidadePorId(usuarioId)).thenReturn(usuarioTeste);

        mockMvc.perform(get("/api/usuarios/{id}", usuarioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuarioId.toString()))
                .andExpect(jsonPath("$.nomeExibicao").value("Maria"))
                .andExpect(jsonPath("$.tipoUsuario").value("ESPECTADOR"))
                .andExpect(jsonPath("$.email").doesNotExist());

        verify(usuarioService).buscarEntidadePorId(usuarioId);
    }

    @Test
    void deveRetornarNotFoundQuandoUsuarioNaoEncontrado() throws Exception {
        UUID idInexistente = UUID.randomUUID();
        when(usuarioService.buscarEntidadePorId(idInexistente))
                .thenThrow(new RecursoNaoEncontradoException("Usuario nao encontrado"));

        mockMvc.perform(get("/api/usuarios/{id}", idInexistente))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Usuario nao encontrado"));

        verify(usuarioService).buscarEntidadePorId(idInexistente);
    }
}
