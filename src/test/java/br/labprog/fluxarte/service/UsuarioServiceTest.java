package br.labprog.fluxarte.service;

import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.model.enums.TipoUsuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioServiceTest extends AbstractServiceTest {

    @Autowired UsuarioService service;
    @Autowired PasswordEncoder passwordEncoder;

    @Test
    void deveCadastrarUsuarioComSenhaCodificadaEValoresPadrao() {
        // cenário e ação
        Usuario salvo = service.cadastrar("Ana", "ana@teste.com", "segredo",
                LocalDate.of(2000, 5, 10));
        flushAndClear();

        // verificação
        Usuario recuperado = usuarioRepository.findById(salvo.getId()).orElseThrow();
        assertEquals("Ana", recuperado.getNome());
        assertEquals("Ana", recuperado.getNomeExibicao());
        assertTrue(passwordEncoder.matches("segredo", recuperado.getSenhaHash()));
        assertNotEquals("segredo", recuperado.getSenhaHash());
        assertEquals(TipoUsuario.ESPECTADOR, recuperado.getTipoUsuario());
        assertTrue(recuperado.getAtivo());
        assertFalse(recuperado.getAceitaConteudoAdulto());
    }

    @Test
    void deveRejeitarEmailDuplicado() {
        service.cadastrar("Ana", "duplicado@teste.com", "senha", null);

        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                () -> service.cadastrar("Outra", "duplicado@teste.com", "senha", null));
        assertEquals("Ja existe um usuario cadastrado com esse email", erro.getMessage());
    }

    @Test
    void deveBuscarUsuarioOuInformarAusencia() {
        Usuario usuario = salvarUsuario("buscar@teste.com");

        assertEquals(usuario.getId(), service.buscarPorId(usuario.getId()).getId());
        NoSuchElementException erro = assertThrows(NoSuchElementException.class,
                () -> service.buscarPorId(UUID.randomUUID()));
        assertTrue(erro.getMessage().startsWith("Usuario nao encontrado:"));
    }

    @Test
    void deveAutenticarSomenteSenhaCorretaEUsuarioAtivo() {
        Usuario usuario = service.cadastrar("Login", "login@teste.com", "correta", null);

        assertTrue(service.autenticar("login@teste.com", "correta"));
        assertFalse(service.autenticar("login@teste.com", "errada"));
        assertFalse(service.autenticar("inexistente@teste.com", "correta"));

        usuario.setAtivo(false);
        usuarioRepository.saveAndFlush(usuario);
        assertFalse(service.autenticar("login@teste.com", "correta"));
    }

    // Contratos esperados pelo material da disciplina; permanecem vermelhos até o service validar os campos.
    @Test
    void deveGerarErroAoCadastrarSemNome() {
        assertThrows(IllegalArgumentException.class,
                () -> service.cadastrar(null, "sem-nome@teste.com", "senha", null));
    }

    @Test
    void deveGerarErroAoCadastrarSemEmail() {
        assertThrows(IllegalArgumentException.class,
                () -> service.cadastrar("Sem email", null, "senha", null));
    }

    @Test
    void deveGerarErroAoCadastrarSemSenha() {
        assertThrows(IllegalArgumentException.class,
                () -> service.cadastrar("Sem senha", "sem-senha@teste.com", null, null));
    }
}
