package br.labprog.fluxarte.service;

import br.labprog.fluxarte.dto.request.AtividadeUsuarioRequest;
import br.labprog.fluxarte.dto.response.AtividadeUsuarioResponse;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.model.enums.TipoAtividade;
import br.labprog.fluxarte.repository.AtividadeUsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import br.labprog.fluxarte.exceptions.RecursoNaoEncontradoException;

import static org.junit.jupiter.api.Assertions.*;

class AtividadeUsuarioServiceTest extends AbstractServiceTest {

    @Autowired AtividadeUsuarioService service;
    @Autowired AtividadeUsuarioRepository repository;

    @Test
    void deveRegistrarBuscaComTermoNormalizado() {
        Usuario usuario = salvarUsuario("atividade-busca@teste.com");

        AtividadeUsuarioRequest request = new AtividadeUsuarioRequest(
                TipoAtividade.BUSCA, "  cinema maranhense  ", 999L, "{\"origem\":\"home\"}");
        AtividadeUsuarioResponse response = service.registrar(usuario.getId(), request);

        assertNotNull(response.id());
        assertEquals("cinema maranhense", response.termoBusca());
        assertNull(response.obraIdReferenciada());
        assertEquals("{\"origem\":\"home\"}", response.metadadosJson());
    }

    @Test
    void deveRegistrarEListarViaDtoComSucesso() {
        Usuario usuario = salvarUsuario("atividade-dto@teste.com");
        ObraAudiovisual obra = salvarObra("Obra DTO Atividade");

        AtividadeUsuarioRequest requestBusca = new AtividadeUsuarioRequest(
                TipoAtividade.BUSCA, "  documentario  ", null, "{\"origem\":\"busca\"}");
        AtividadeUsuarioResponse responseBusca = service.registrar(usuario.getId(), requestBusca);

        assertNotNull(responseBusca.id());
        assertEquals(TipoAtividade.BUSCA, responseBusca.tipoAtividade());
        assertEquals("documentario", responseBusca.termoBusca());
        assertNull(responseBusca.obraIdReferenciada());

        AtividadeUsuarioRequest requestObra = new AtividadeUsuarioRequest(
                TipoAtividade.CLIQUE_OBRA, null, obra.getId(), null);
        AtividadeUsuarioResponse responseObra = service.registrar(usuario.getId(), requestObra);

        assertNotNull(responseObra.id());
        assertEquals(TipoAtividade.CLIQUE_OBRA, responseObra.tipoAtividade());
        assertEquals(obra.getId(), responseObra.obraIdReferenciada());

        List<AtividadeUsuarioResponse> lista = service.listarPorUsuario(usuario.getId());
        assertEquals(2, lista.size());
        assertEquals(1, service.listarPorUsuarioETipo(usuario.getId(), TipoAtividade.BUSCA).size());
    }

    @Test
    void deveRegistrarAtividadeReferenciadaEValidarAObra() {
        Usuario usuario = salvarUsuario("atividade-obra@teste.com");
        ObraAudiovisual obra = salvarObra("Atividade obra");

        AtividadeUsuarioRequest request = new AtividadeUsuarioRequest(
                TipoAtividade.CLIQUE_OBRA, "ignorado", obra.getId(), null);
        AtividadeUsuarioResponse response = service.registrar(usuario.getId(), request);

        assertNull(response.termoBusca());
        assertEquals(obra.getId(), response.obraIdReferenciada());

        AtividadeUsuarioRequest invalida = new AtividadeUsuarioRequest(
                TipoAtividade.VIEW_TRAILER, null, Long.MAX_VALUE, null);
        assertThrows(RecursoNaoEncontradoException.class,
                () -> service.registrar(usuario.getId(), invalida));
    }

    @Test
    void deveRejeitarRequestNulo() {
        Usuario usuario = salvarUsuario("atividade-invalida@teste.com");
        assertThrows(IllegalArgumentException.class,
                () -> service.registrar(usuario.getId(), null));
    }

    @Test
    void deveListarPorUsuarioEPorTipoMaisRecentePrimeiro() {
        Usuario usuario = salvarUsuario("listar-atividade@teste.com");
        ObraAudiovisual obra = salvarObra("Listar atividade");
        service.registrar(usuario.getId(), new AtividadeUsuarioRequest(TipoAtividade.BUSCA, "primeira", null, null));
        AtividadeUsuarioResponse recente = service.registrar(
                usuario.getId(), new AtividadeUsuarioRequest(TipoAtividade.CLIQUE_OBRA, null, obra.getId(), null));

        assertEquals(2, service.listarPorUsuario(usuario.getId()).size());
        assertEquals(recente.id(), service.listarPorUsuario(usuario.getId()).get(0).id());
        assertEquals(1, service.listarPorUsuarioETipo(usuario.getId(), TipoAtividade.BUSCA).size());
        assertEquals(2, repository.count());
    }
}
