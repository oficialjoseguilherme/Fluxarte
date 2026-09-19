package br.labprog.fluxarte.service;

import br.labprog.fluxarte.dto.request.LoginRequest;
import br.labprog.fluxarte.dto.request.RefreshTokenRequest;
import br.labprog.fluxarte.dto.response.LoginResponse;
import br.labprog.fluxarte.model.RefreshToken;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest extends AbstractServiceTest {

    @Autowired AuthService service;
    @Autowired UsuarioService usuarioService;
    @Autowired RefreshTokenRepository refreshTokenRepository;

    @Test
    void deveFazerLoginEEmitirTokenSeguroPorTrintaDias() {
        Usuario usuario = usuarioService.cadastrar("Auth", "auth@teste.com", "senha", null);
        LocalDateTime antes = LocalDateTime.now().plusDays(30).minusSeconds(1);

        RefreshToken token = service.login("auth@teste.com", "senha");
        LocalDateTime depois = LocalDateTime.now().plusDays(30).plusSeconds(1);

        assertNotNull(token.getId());
        assertEquals(usuario.getId(), token.getUsuario().getId());
        assertFalse(token.getRevogado());
        assertEquals(43, token.getToken().length());
        assertFalse(token.getExpiraEm().isBefore(antes));
        assertFalse(token.getExpiraEm().isAfter(depois));
    }

    @Test
    void deveFazerLoginViaDto() {
        Usuario usuario = usuarioService.cadastrar("AuthDto", "authdto@teste.com", "senha123", null);
        LoginResponse response = service.login(new LoginRequest("authdto@teste.com", "senha123"));

        assertNotNull(response);
        assertNotNull(response.refreshToken());
        assertEquals("Bearer", response.tipo());
        assertEquals(usuario.getId(), response.usuarioId());
    }

    @Test
    void deveOcultarMotivoDasCredenciaisInvalidas() {
        usuarioService.cadastrar("Auth", "credencial@teste.com", "certa", null);

        BadCredentialsException email = assertThrows(BadCredentialsException.class,
                () -> service.login("nao-existe@teste.com", "certa"));
        BadCredentialsException senha = assertThrows(BadCredentialsException.class,
                () -> service.login("credencial@teste.com", "errada"));
        assertEquals("Credenciais invalidas", email.getMessage());
        assertEquals("Credenciais invalidas", senha.getMessage());
    }

    @Test
    void deveRotacionarRefreshToken() {
        usuarioService.cadastrar("Auth", "rotacao@teste.com", "senha", null);
        RefreshToken antigo = service.login("rotacao@teste.com", "senha");

        RefreshToken novo = service.renovar(antigo.getToken());
        flushAndClear();

        assertNotEquals(antigo.getToken(), novo.getToken());
        assertTrue(refreshTokenRepository.findById(antigo.getId()).orElseThrow().getRevogado());
        assertFalse(refreshTokenRepository.findById(novo.getId()).orElseThrow().getRevogado());
    }

    @Test
    void deveRotacionarViaDto() {
        usuarioService.cadastrar("AuthRotDto", "rotdto@teste.com", "senha123", null);
        LoginResponse login = service.login(new LoginRequest("rotdto@teste.com", "senha123"));

        LoginResponse novo = service.renovar(new RefreshTokenRequest(login.refreshToken()));
        flushAndClear();

        assertNotEquals(login.refreshToken(), novo.refreshToken());
        assertEquals(login.usuarioId(), novo.usuarioId());
    }

    @Test
    void deveRevogarTokenExpiradoAoTentarRenovar() {
        Usuario usuario = salvarUsuario("token-exp@teste.com");
        RefreshToken expirado = refreshTokenRepository.saveAndFlush(token(usuario, "token-expirado",
                LocalDateTime.now().minusMinutes(5)));

        BadCredentialsException erro = assertThrows(BadCredentialsException.class,
                () -> service.renovar(expirado.getToken()));
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
                () -> service.renovar(revogado.getToken()));
        assertEquals("Tentativa de reuso de sessao detectada", erro.getMessage());

        assertTrue(refreshTokenRepository.findById(sessaoAtiva.getId()).orElseThrow().getRevogado());
    }

    @Test
    void deveRejeitarTokenInexistenteOuDeUsuarioInativo() {
        BadCredentialsException inexistente = assertThrows(BadCredentialsException.class,
                () -> service.renovar("inexistente"));
        assertEquals("Refresh token invalido", inexistente.getMessage());

        Usuario usuario = salvarUsuario("token-inativo@teste.com");
        RefreshToken inativo = refreshTokenRepository.saveAndFlush(token(usuario, "inativo",
                LocalDateTime.now().plusDays(1)));
        usuario.setAtivo(false);
        usuarioRepository.saveAndFlush(usuario);
        BadCredentialsException erroInativo = assertThrows(BadCredentialsException.class,
                () -> service.renovar(inativo.getToken()));
        assertEquals("Usuario inativo", erroInativo.getMessage());
    }

    @Test
    void deveFazerLogoutDeFormaIdempotente() {
        usuarioService.cadastrar("Auth", "logout@teste.com", "senha", null);
        RefreshToken token = service.login("logout@teste.com", "senha");

        service.logout(token.getToken());
        service.logout(token.getToken());
        service.logout("inexistente");

        assertTrue(refreshTokenRepository.findById(token.getId()).orElseThrow().getRevogado());
    }

    @Test
    void deveEncerrarTodasAsSessoesDoUsuario() {
        Usuario usuario = usuarioService.cadastrar("Auth", "todas@teste.com", "senha", null);
        service.login("todas@teste.com", "senha");
        service.login("todas@teste.com", "senha");

        service.logoutTodasSessoes(usuario.getId());
        flushAndClear();

        assertTrue(refreshTokenRepository.findByUsuarioIdAndRevogadoFalse(usuario.getId()).isEmpty());
    }

    private RefreshToken token(Usuario usuario, String valor, LocalDateTime expiraEm) {
        return RefreshToken.builder()
                .usuario(usuario).token(valor).expiraEm(expiraEm).revogado(false).build();
    }
}
