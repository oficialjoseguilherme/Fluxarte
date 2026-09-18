package br.labprog.fluxarte.model.repository;

import br.labprog.fluxarte.model.RefreshToken;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.repository.RefreshTokenRepository;
import br.labprog.fluxarte.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenRepositoryTest extends AbstractRepositoryTest {

    @Autowired RefreshTokenRepository repository;
    @Autowired UsuarioRepository usuarioRepository;

    @Test
    void deveConsultarSomenteTokensNaoRevogadosEManterPadrao() {
        Usuario usuario = usuarioRepository.save(ModelFixtures.usuario("token@teste.com"));
        RefreshToken ativo = repository.saveAndFlush(token(usuario, "token-ativo", null));
        RefreshToken revogado = repository.saveAndFlush(token(usuario, "token-revogado", true));
        flushAndClear();

        RefreshToken recuperado = repository.findByTokenAndRevogadoFalse("token-ativo").orElseThrow();
        assertFalse(recuperado.getRevogado());
        assertNotNull(recuperado.getCriadoEm());
        assertEquals(ativo.getId(), recuperado.getId());
        assertTrue(repository.findByTokenAndRevogadoFalse("token-revogado").isEmpty());
        assertEquals(1, repository.findByUsuarioIdAndRevogadoFalse(usuario.getId()).size());
        assertNotNull(revogado.getId());
    }

    @Test
    void naoDevePermitirTokenDuplicado() {
        Usuario usuario = usuarioRepository.save(ModelFixtures.usuario("token-duplicado@teste.com"));
        repository.saveAndFlush(token(usuario, "mesmo-token", false));

        assertThrows(DataIntegrityViolationException.class,
                () -> repository.saveAndFlush(token(usuario, "mesmo-token", false)));
    }

    private RefreshToken token(Usuario usuario, String valor, Boolean revogado) {
        RefreshToken.RefreshTokenBuilder builder = RefreshToken.builder()
                .usuario(usuario)
                .token(valor)
                .expiraEm(LocalDateTime.now().plusDays(30));
        if (revogado != null) {
            builder.revogado(revogado);
        }
        return builder.build();
    }
}
