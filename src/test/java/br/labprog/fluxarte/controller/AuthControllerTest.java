package br.labprog.fluxarte.controller;

import br.labprog.fluxarte.dto.auth.LoginRequest;
import br.labprog.fluxarte.dto.auth.LogoutRequest;
import br.labprog.fluxarte.dto.auth.RegistroRequest;
import br.labprog.fluxarte.dto.auth.RenovarTokenRequest;
import br.labprog.fluxarte.exceptions.AcessoNegadoException;
import br.labprog.fluxarte.exceptions.ConflitoException;
import br.labprog.fluxarte.exceptions.ExceptionGlobalHandler;
import br.labprog.fluxarte.model.RefreshToken;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.model.enums.TipoUsuario;
import br.labprog.fluxarte.security.JwtService;
import br.labprog.fluxarte.service.AuthService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private Usuario usuarioTeste;
    private RefreshToken refreshTokenTeste;
    private UUID usuarioId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new ExceptionGlobalHandler())
                .build();

        usuarioId = UUID.randomUUID();
        usuarioTeste = Usuario.builder()
                .id(usuarioId)
                .nome("Carlos Eduardo")
                .email("carlos@fluxarte.com")
                .nomeExibicao("Carlos")
                .tipoUsuario(TipoUsuario.ESPECTADOR)
                .ativo(true)
                .build();

        refreshTokenTeste = RefreshToken.builder()
                .id(1L)
                .usuario(usuarioTeste)
                .token("refresh-token-valido-123")
                .expiraEm(LocalDateTime.now().plusDays(30))
                .build();
    }

    @Test
    void deveRegistrarUsuarioComSucesso() throws Exception {
        RegistroRequest request = new RegistroRequest("Carlos Eduardo", "carlos@fluxarte.com", "senha123", LocalDate.of(1990, 1, 1));

        when(usuarioService.cadastrar(eq("Carlos Eduardo"), eq("carlos@fluxarte.com"), eq("senha123"), any()))
                .thenReturn(usuarioTeste);

        mockMvc.perform(post("/api/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(usuarioId.toString()))
                .andExpect(jsonPath("$.nome").value("Carlos Eduardo"))
                .andExpect(jsonPath("$.email").value("carlos@fluxarte.com"))
                .andExpect(jsonPath("$.tipoUsuario").value("ESPECTADOR"));

        verify(usuarioService).cadastrar(eq("Carlos Eduardo"), eq("carlos@fluxarte.com"), eq("senha123"), any());
    }

    @Test
    void deveRetornarConflitoAoRegistrarComEmailExistente() throws Exception {
        RegistroRequest request = new RegistroRequest("Carlos Eduardo", "carlos@fluxarte.com", "senha123", LocalDate.of(1990, 1, 1));

        when(usuarioService.cadastrar(any(), any(), any(), any()))
                .thenThrow(new ConflitoException("Ja existe um usuario cadastrado com esse email"));

        mockMvc.perform(post("/api/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensagem").value("Ja existe um usuario cadastrado com esse email"));
    }

    @Test
    void deveFazerLoginComSucesso() throws Exception {
        LoginRequest request = new LoginRequest("carlos@fluxarte.com", "senha123");

        when(authService.login("carlos@fluxarte.com", "senha123")).thenReturn(refreshTokenTeste);
        when(jwtService.gerarToken(usuarioId, "carlos@fluxarte.com")).thenReturn("jwt-access-token-xyz");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-access-token-xyz"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token-valido-123"))
                .andExpect(jsonPath("$.tipo").value("Bearer"));

        verify(authService).login("carlos@fluxarte.com", "senha123");
        verify(jwtService).gerarToken(usuarioId, "carlos@fluxarte.com");
    }

    @Test
    void deveRetornarForbiddenQuandoCredenciaisInvalidasNoLogin() throws Exception {
        LoginRequest request = new LoginRequest("carlos@fluxarte.com", "senha-errada");

        when(authService.login("carlos@fluxarte.com", "senha-errada"))
                .thenThrow(new AcessoNegadoException("Email ou senha invalidos"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.mensagem").value("Email ou senha invalidos"));
    }

    @Test
    void deveRenovarTokenComSucesso() throws Exception {
        RenovarTokenRequest request = new RenovarTokenRequest("refresh-token-antigo");

        when(authService.renovar("refresh-token-antigo")).thenReturn(refreshTokenTeste);
        when(jwtService.gerarToken(usuarioId, "carlos@fluxarte.com")).thenReturn("novo-jwt-access-token");

        mockMvc.perform(post("/api/auth/renovar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("novo-jwt-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token-valido-123"))
                .andExpect(jsonPath("$.tipo").value("Bearer"));

        verify(authService).renovar("refresh-token-antigo");
    }

    @Test
    void deveFazerLogoutComSucesso() throws Exception {
        LogoutRequest request = new LogoutRequest("refresh-token-para-revogar");
        doNothing().when(authService).logout("refresh-token-para-revogar");

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(authService).logout("refresh-token-para-revogar");
    }
}
