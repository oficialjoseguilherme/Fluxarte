package br.labprog.fluxarte.model.repository;

import br.labprog.fluxarte.model.GeneroObra;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.model.enums.NomeGenero;
import br.labprog.fluxarte.model.enums.TipoUsuario;
import br.labprog.fluxarte.repository.GeneroObraRepository;
import br.labprog.fluxarte.repository.ObraAudiovisualRepository;
import br.labprog.fluxarte.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioRepositoryTest extends AbstractRepositoryTest {

    @Autowired UsuarioRepository repository;
    @Autowired GeneroObraRepository generoRepository;
    @Autowired ObraAudiovisualRepository obraRepository;

    @Test
    void deveSalvarConsultarEManterValoresPadrao() {
        // cenário
        Usuario usuario = ModelFixtures.usuario("usuario@teste.com");

        // ação
        Usuario salvo = repository.saveAndFlush(usuario);
        UUID id = salvo.getId();
        flushAndClear();

        // verificação
        Usuario recuperado = repository.findById(id).orElseThrow();
        assertNotNull(recuperado.getId());
        assertEquals("Pessoa Teste", recuperado.getNome());
        assertEquals("usuario@teste.com", recuperado.getEmail());
        assertEquals("hash-seguro", recuperado.getSenhaHash());
        assertEquals(TipoUsuario.ESPECTADOR, recuperado.getTipoUsuario());
        assertFalse(recuperado.getAceitaConteudoAdulto());
        assertTrue(recuperado.getAtivo());
        assertNotNull(recuperado.getCriadoEm());
        assertNotNull(recuperado.getAtualizadoEm());
        assertTrue(repository.findByEmail("usuario@teste.com").isPresent());
        assertTrue(repository.existsByEmail("usuario@teste.com"));
    }

    @Test
    void devePersistirGenerosPreferidosEFavoritos() {
        // cenário
        GeneroObra genero = generoRepository.save(ModelFixtures.genero(NomeGenero.DOCUMENTARIO));
        ObraAudiovisual obra = obraRepository.save(ModelFixtures.obra("Obra favorita"));
        Usuario usuario = ModelFixtures.usuario("preferencias@teste.com");
        usuario.getGenerosPreferidos().add(genero);
        usuario.getFavoritos().add(obra);

        // ação
        UUID id = repository.saveAndFlush(usuario).getId();
        flushAndClear();

        // verificação
        Usuario recuperado = repository.findById(id).orElseThrow();
        assertEquals(1, recuperado.getGenerosPreferidos().size());
        assertEquals(1, recuperado.getFavoritos().size());
    }

    @Test
    void deveRemoverUsuario() {
        Usuario salvo = repository.saveAndFlush(ModelFixtures.usuario("remover@teste.com"));
        UUID id = salvo.getId();

        repository.deleteById(id);
        repository.flush();

        assertFalse(repository.findById(id).isPresent());
    }

    @Test
    void naoDevePermitirEmailDuplicado() {
        repository.saveAndFlush(ModelFixtures.usuario("duplicado@teste.com"));

        assertThrows(DataIntegrityViolationException.class,
                () -> repository.saveAndFlush(ModelFixtures.usuario("duplicado@teste.com")));
    }
}
