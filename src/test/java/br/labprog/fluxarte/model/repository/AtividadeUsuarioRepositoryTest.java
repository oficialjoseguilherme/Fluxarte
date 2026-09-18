package br.labprog.fluxarte.model.repository;

import br.labprog.fluxarte.model.AtividadeUsuario;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.model.enums.TipoAtividade;
import br.labprog.fluxarte.repository.AtividadeUsuarioRepository;
import br.labprog.fluxarte.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class AtividadeUsuarioRepositoryTest extends AbstractRepositoryTest {

    @Autowired AtividadeUsuarioRepository repository;
    @Autowired UsuarioRepository usuarioRepository;

    @Test
    void deveSalvarFiltrarEOrdenarAtividades() {
        Usuario usuario = usuarioRepository.save(ModelFixtures.usuario("atividade@teste.com"));
        AtividadeUsuario busca = repository.saveAndFlush(atividade(usuario, TipoAtividade.BUSCA, "cinema"));
        AtividadeUsuario clique = repository.saveAndFlush(atividade(usuario, TipoAtividade.CLIQUE_OBRA, null));
        flushAndClear();

        var todas = repository.findByUsuarioIdOrderByRegistradoEmDesc(usuario.getId());
        var buscas = repository.findByUsuarioIdAndTipoAtividadeOrderByRegistradoEmDesc(
                usuario.getId(), TipoAtividade.BUSCA);
        assertEquals(2, todas.size());
        assertEquals(clique.getId(), todas.get(0).getId());
        assertEquals(1, buscas.size());
        assertEquals("cinema", buscas.get(0).getTermoBusca());
        assertEquals(99L, buscas.get(0).getObraIdReferenciada());
        assertEquals("{\"origem\":\"teste\"}", buscas.get(0).getMetadadosJson());
        assertNotNull(busca.getRegistradoEm());
    }

    private AtividadeUsuario atividade(Usuario usuario, TipoAtividade tipo, String termo) {
        return AtividadeUsuario.builder()
                .usuario(usuario)
                .tipoAtividade(tipo)
                .termoBusca(termo)
                .obraIdReferenciada(99L)
                .metadadosJson("{\"origem\":\"teste\"}")
                .build();
    }
}
