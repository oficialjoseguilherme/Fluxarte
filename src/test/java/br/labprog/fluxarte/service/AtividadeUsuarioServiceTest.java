package br.labprog.fluxarte.service;

import br.labprog.fluxarte.model.AtividadeUsuario;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.model.enums.TipoAtividade;
import br.labprog.fluxarte.repository.AtividadeUsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class AtividadeUsuarioServiceTest extends AbstractServiceTest {

    @Autowired AtividadeUsuarioService service;
    @Autowired AtividadeUsuarioRepository repository;

    @Test
    void deveRegistrarBuscaComTermoNormalizado() {
        Usuario usuario = salvarUsuario("atividade-busca@teste.com");

        AtividadeUsuario atividade = service.registrar(usuario.getId(), TipoAtividade.BUSCA,
                "  cinema maranhense  ", 999L, "{\"origem\":\"home\"}");

        assertNotNull(atividade.getId());
        assertEquals("cinema maranhense", atividade.getTermoBusca());
        assertNull(atividade.getObraIdReferenciada());
        assertEquals("{\"origem\":\"home\"}", atividade.getMetadadosJson());
    }

    @Test
    void deveRegistrarAtividadeReferenciadaEValidarAObra() {
        Usuario usuario = salvarUsuario("atividade-obra@teste.com");
        ObraAudiovisual obra = salvarObra("Atividade obra");

        AtividadeUsuario atividade = service.registrar(usuario.getId(), TipoAtividade.CLIQUE_OBRA,
                "ignorado", obra.getId(), null);

        assertNull(atividade.getTermoBusca());
        assertEquals(obra.getId(), atividade.getObraIdReferenciada());
        assertThrows(NoSuchElementException.class,
                () -> service.registrar(usuario.getId(), TipoAtividade.VIEW_TRAILER,
                        null, Long.MAX_VALUE, null));
    }

    @Test
    void deveValidarTipoETermosObrigatorios() {
        Usuario usuario = salvarUsuario("atividade-invalida@teste.com");
        assertEquals("Tipo da atividade e obrigatorio",
                assertThrows(IllegalArgumentException.class,
                        () -> service.registrar(usuario.getId(), null, null, null, null)).getMessage());
        assertEquals("Atividade de busca exige o termo pesquisado",
                assertThrows(IllegalArgumentException.class,
                        () -> service.registrar(usuario.getId(), TipoAtividade.BUSCA,
                                " ", null, null)).getMessage());
        assertThrows(IllegalArgumentException.class,
                () -> service.registrar(usuario.getId(), TipoAtividade.VIEW_DIRETOR,
                        null, null, null));
    }

    @Test
    void deveListarPorUsuarioEPorTipoMaisRecentePrimeiro() {
        Usuario usuario = salvarUsuario("listar-atividade@teste.com");
        ObraAudiovisual obra = salvarObra("Listar atividade");
        service.registrar(usuario.getId(), TipoAtividade.BUSCA, "primeira", null, null);
        AtividadeUsuario recente = service.registrar(usuario.getId(), TipoAtividade.CLIQUE_OBRA,
                null, obra.getId(), null);

        assertEquals(2, service.listarPorUsuario(usuario.getId()).size());
        assertEquals(recente.getId(), service.listarPorUsuario(usuario.getId()).get(0).getId());
        assertEquals(1, service.listarPorUsuarioETipo(usuario.getId(), TipoAtividade.BUSCA).size());
        assertEquals(2, repository.count());
    }
}
