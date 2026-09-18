package br.labprog.fluxarte.service;

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
    void deveRejeitarTokenInexistenteExpiradoOuDeUsuarioInativo() {
        BadCredentialsException inexistente = assertThrows(BadCredentialsException.class,
                () -> service.renovar("inexistente"));
        assertEquals("Refresh token invalido", inexistente.getMessage());

        Usuario usuario = salvarUsuario("token-erros@teste.com");
        RefreshToken expirado = refreshTokenRepository.saveAndFlush(token(usuario, "expirado",
                LocalDateTime.now().minusMinutes(1)));
        BadCredentialsException erroExpirado = assertThrows(BadCredentialsException.class,
                () -> service.renovar(expirado.getToken()));
        assertEquals("Refresh token expirado", erroExpirado.getMessage());

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
