package br.labprog.fluxarte.service;

import br.labprog.fluxarte.dto.request.UsuarioCadastroRequest;
import br.labprog.fluxarte.dto.response.UsuarioResponse;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.model.enums.TipoUsuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.labprog.fluxarte.exceptions.ConflitoException;
import br.labprog.fluxarte.exceptions.RecursoNaoEncontradoException;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioServiceTest extends AbstractServiceTest {

    @Autowired UsuarioService service;
    @Autowired PasswordEncoder passwordEncoder;

    @Test
    void deveCadastrarUsuarioComSenhaCodificadaEValoresPadrao() {
        UsuarioCadastroRequest request = new UsuarioCadastroRequest(
                "Ana", "ana@teste.com", "segredo", LocalDate.of(2000, 5, 10), null, false);
        UsuarioResponse salvo = service.cadastrar(request);
        flushAndClear();

        Usuario recuperado = usuarioRepository.findById(salvo.id()).orElseThrow();
        assertEquals("Ana", recuperado.getNome());
        assertEquals("Ana", recuperado.getNomeExibicao());
        assertTrue(passwordEncoder.matches("segredo", recuperado.getSenhaHash()));
        assertNotEquals("segredo", recuperado.getSenhaHash());
        assertEquals(TipoUsuario.ESPECTADOR, recuperado.getTipoUsuario());
        assertTrue(recuperado.getAtivo());
        assertFalse(recuperado.getAceitaConteudoAdulto());
    }

    @Test
    void deveCadastrarViaDtoComSucesso() {
        UsuarioCadastroRequest request = new UsuarioCadastroRequest(
                "Carlos", "carlos@teste.com", "senhaForte123",
                LocalDate.of(1995, 3, 15), "Carlinhos", true);

        UsuarioResponse response = service.cadastrar(request);
        flushAndClear();

        assertNotNull(response.id());
        assertEquals("Carlos", response.nome());
        assertEquals("carlos@teste.com", response.email());
        assertEquals("Carlinhos", response.nomeExibicao());
        assertTrue(response.aceitaConteudoAdulto());
        assertTrue(response.ativo());
    }

    @Test
    void deveRejeitarEmailDuplicado() {
        UsuarioCadastroRequest request = new UsuarioCadastroRequest(
                "Ana", "duplicado@teste.com", "senha", LocalDate.of(2000, 1, 1), null, false);
        service.cadastrar(request);

        UsuarioCadastroRequest duplicado = new UsuarioCadastroRequest(
                "Outra", "duplicado@teste.com", "senha", LocalDate.of(2000, 1, 1), null, false);
        ConflitoException erro = assertThrows(ConflitoException.class,
                () -> service.cadastrar(duplicado));
        assertEquals("Ja existe um usuario cadastrado com esse email", erro.getMessage());
    }

    @Test
    void deveBuscarUsuarioOuInformarAusencia() {
        Usuario usuario = salvarUsuario("buscar@teste.com");

        assertEquals(usuario.getId(), service.buscarPorId(usuario.getId()).id());
        assertEquals(usuario.getId(), service.buscarEntidadePorId(usuario.getId()).getId());

        RecursoNaoEncontradoException erro = assertThrows(RecursoNaoEncontradoException.class,
                () -> service.buscarPorId(UUID.randomUUID()));
        assertEquals("Usuario nao encontrado", erro.getMessage());
    }

    @Test
    void deveGerarErroAoCadastrarSemNome() {
        UsuarioCadastroRequest semNome = new UsuarioCadastroRequest(
                null, "sem-nome@teste.com", "senha", LocalDate.of(2000, 1, 1), null, false);
        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(semNome));
    }

    @Test
    void deveGerarErroAoCadastrarSemEmail() {
        UsuarioCadastroRequest semEmail = new UsuarioCadastroRequest(
                "Sem email", null, "senha", LocalDate.of(2000, 1, 1), null, false);
        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(semEmail));
    }

    @Test
    void deveGerarErroAoCadastrarSemSenha() {
        UsuarioCadastroRequest semSenha = new UsuarioCadastroRequest(
                "Sem senha", "sem-senha@teste.com", null, LocalDate.of(2000, 1, 1), null, false);
        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(semSenha));
    }
}
