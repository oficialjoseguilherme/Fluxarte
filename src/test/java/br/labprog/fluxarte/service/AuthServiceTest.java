package br.labprog.fluxarte.service;

import br.labprog.fluxarte.dto.request.LoginRequest;
import br.labprog.fluxarte.dto.request.RefreshTokenRequest;
import br.labprog.fluxarte.dto.request.UsuarioCadastroRequest;
import br.labprog.fluxarte.dto.response.LoginResponse;
import br.labprog.fluxarte.dto.response.UsuarioResponse;
import br.labprog.fluxarte.model.RefreshToken;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest extends AbstractServiceTest {

    @Autowired AuthService service;
    @Autowired UsuarioService usuarioService;
    @Autowired RefreshTokenRepository refreshTokenRepository;

    @Test
    void deveFazerLoginEEmitirTokenSeguroPorTrintaDias() {
        Usuario usuario = cadastrarUsuario("auth@teste.com", "senha");
        LocalDateTime antes = LocalDateTime.now().plusDays(30).minusSeconds(1);

        LoginResponse response = service.login(new LoginRequest("auth@teste.com", "senha"));
        LocalDateTime depois = LocalDateTime.now().plusDays(30).plusSeconds(1);

        assertNotNull(response);
        assertEquals(usuario.getId(), response.usuarioId());
        assertEquals("Bearer", response.tipo());
        assertEquals("Auth", response.nome());
        assertEquals("auth@teste.com", response.email());
        assertEquals(43, response.refreshToken().length());
        assertFalse(response.expiraEm().isBefore(antes));
        assertFalse(response.expiraEm().isAfter(depois));

        RefreshToken token = refreshTokenRepository.findByToken(response.refreshToken()).orElseThrow();
        assertFalse(token.getRevogado());
    }

    @Test
    void deveOcultarMotivoDasCredenciaisInvalidas() {
        cadastrarUsuario("credencial@teste.com", "certa");

        BadCredentialsException email = assertThrows(BadCredentialsException.class,
                () -> service.login(new LoginRequest("nao-existe@teste.com", "certa")));
        BadCredentialsException senha = assertThrows(BadCredentialsException.class,
                () -> service.login(new LoginRequest("credencial@teste.com", "errada")));
        assertEquals("Credenciais invalidas", email.getMessage());
        assertEquals("Credenciais invalidas", senha.getMessage());
    }

    @Test
    void deveRotacionarRefreshToken() {
        cadastrarUsuario("rotacao@teste.com", "senha");
        LoginResponse antigo = service.login(new LoginRequest("rotacao@teste.com", "senha"));

        LoginResponse novo = service.renovar(new RefreshTokenRequest(antigo.refreshToken()));
        flushAndClear();

        assertNotEquals(antigo.refreshToken(), novo.refreshToken());
        assertEquals(antigo.usuarioId(), novo.usuarioId());
        assertTrue(refreshTokenRepository.findByToken(antigo.refreshToken()).orElseThrow().getRevogado());
        assertFalse(refreshTokenRepository.findByToken(novo.refreshToken()).orElseThrow().getRevogado());
    }

    @Test
    void deveRevogarTokenExpiradoAoTentarRenovar() {
        Usuario usuario = salvarUsuario("token-exp@teste.com");
        RefreshToken expirado = refreshTokenRepository.saveAndFlush(token(usuario, "token-expirado",
                LocalDateTime.now().minusMinutes(5)));

        BadCredentialsException erro = assertThrows(BadCredentialsException.class,
                () -> service.renovar(new RefreshTokenRequest(expirado.getToken())));
        assertEquals("Refresh token expirado", erro.getMessage());

        RefreshToken atualizado = refreshTokenRepository.findById(expirado.getId()).orElseThrow();
        assertTrue(atualizado.getRevogado());
    }

    @Test
    void deveDetectarTentativaDeReusoDeSessaoRevogada() {
        Usuario usuario = salvarUsuario("token-reuso@teste.com");
        RefreshToken revogado = refreshTokenRepository.saveAndFlush(
                RefreshToken.builder()
                        .usuario(usuario)
                        .token("token-ja-revogado")
                        .expiraEm(LocalDateTime.now().plusDays(10))
                        .revogado(true)
                        .build());

        RefreshToken sessaoAtiva = refreshTokenRepository.saveAndFlush(
                RefreshToken.builder()
                        .usuario(usuario)
                        .token("sessao-ativa")
                        .expiraEm(LocalDateTime.now().plusDays(10))
                        .revogado(false)
                        .build());

        BadCredentialsException erro = assertThrows(BadCredentialsException.class,
                () -> service.renovar(new RefreshTokenRequest(revogado.getToken())));
        assertEquals("Tentativa de reuso de sessao detectada", erro.getMessage());

        assertTrue(refreshTokenRepository.findById(sessaoAtiva.getId()).orElseThrow().getRevogado());
    }

    @Test
    void deveRejeitarTokenInexistenteOuDeUsuarioInativo() {
        BadCredentialsException inexistente = assertThrows(BadCredentialsException.class,
                () -> service.renovar(new RefreshTokenRequest("inexistente")));
        assertEquals("Refresh token invalido", inexistente.getMessage());

        Usuario usuario = salvarUsuario("token-inativo@teste.com");
        RefreshToken inativo = refreshTokenRepository.saveAndFlush(token(usuario, "inativo",
                LocalDateTime.now().plusDays(1)));
        usuario.setAtivo(false);
        usuarioRepository.saveAndFlush(usuario);
        BadCredentialsException erroInativo = assertThrows(BadCredentialsException.class,
                () -> service.renovar(new RefreshTokenRequest(inativo.getToken())));
        assertEquals("Usuario inativo", erroInativo.getMessage());
    }

    @Test
    void deveFazerLogoutDeFormaIdempotente() {
        cadastrarUsuario("logout@teste.com", "senha");
        LoginResponse login = service.login(new LoginRequest("logout@teste.com", "senha"));

        service.logout(new RefreshTokenRequest(login.refreshToken()));
        service.logout(new RefreshTokenRequest(login.refreshToken()));
        service.logout(new RefreshTokenRequest("inexistente"));
        service.logout(login.refreshToken());

        assertTrue(refreshTokenRepository.findByToken(login.refreshToken()).orElseThrow().getRevogado());
    }

    @Test
    void deveEncerrarTodasAsSessoesDoUsuario() {
        Usuario usuario = cadastrarUsuario("todas@teste.com", "senha");
        service.login(new LoginRequest("todas@teste.com", "senha"));
        service.login(new LoginRequest("todas@teste.com", "senha"));

        service.logoutTodasSessoes(usuario.getId());
        flushAndClear();

        assertTrue(refreshTokenRepository.findByUsuarioIdAndRevogadoFalse(usuario.getId()).isEmpty());
    }

    private Usuario cadastrarUsuario(String email, String senha) {
        UsuarioResponse response = usuarioService.cadastrar(new UsuarioCadastroRequest(
                "Auth", email, senha, LocalDate.of(2000, 1, 1), null, false));
        return usuarioRepository.findById(response.id()).orElseThrow();
    }

    private RefreshToken token(Usuario usuario, String valor, LocalDateTime expiraEm) {
        return RefreshToken.builder()
                .usuario(usuario).token(valor).expiraEm(expiraEm).revogado(false).build();
    }
}
