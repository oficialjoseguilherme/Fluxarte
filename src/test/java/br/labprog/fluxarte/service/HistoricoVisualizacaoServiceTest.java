package br.labprog.fluxarte.service;

import br.labprog.fluxarte.dto.request.HistoricoVisualizacaoRequest;
import br.labprog.fluxarte.dto.response.HistoricoVisualizacaoResponse;
import br.labprog.fluxarte.model.MidiaStreaming;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.model.enums.TipoMidia;
import br.labprog.fluxarte.repository.HistoricoVisualizacaoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class HistoricoVisualizacaoServiceTest extends AbstractServiceTest {

    @Autowired HistoricoVisualizacaoService service;
    @Autowired HistoricoVisualizacaoRepository repository;

    @Test
    void deveRegistrarUmaLinhaPorSessaoEAssociarObraDaMidia() {
        Usuario usuario = salvarUsuario("historico-service@teste.com");
        ObraAudiovisual obra = salvarObra("Histórico service");
        MidiaStreaming midia = salvarMidia(obra, TipoMidia.PRINCIPAL);

        HistoricoVisualizacaoResponse primeiro = service.registrar(
                usuario.getId(), new HistoricoVisualizacaoRequest(midia.getId(), 120, null));
        HistoricoVisualizacaoResponse segundo = service.registrar(
                usuario.getId(), new HistoricoVisualizacaoRequest(midia.getId(), 600, true));

        assertNotEquals(primeiro.id(), segundo.id());
        assertEquals(2, repository.count());
        assertEquals(obra.getId(), segundo.obraId());
        assertEquals(midia.getId(), segundo.midiaId());
        assertFalse(primeiro.concluido());
        assertTrue(segundo.concluido());
        assertNotNull(segundo.dataVisualizacao());
    }

    @Test
    void deveRegistrarEListarViaDtoComSucesso() {
        Usuario usuario = salvarUsuario("historico-dto@teste.com");
        ObraAudiovisual obra = salvarObra("Histórico DTO");
        MidiaStreaming midia = salvarMidia(obra, TipoMidia.PRINCIPAL);

        HistoricoVisualizacaoRequest request = new HistoricoVisualizacaoRequest(midia.getId(), 450, true);
        HistoricoVisualizacaoResponse response = service.registrar(usuario.getId(), request);

        assertNotNull(response.id());
        assertEquals(obra.getId(), response.obraId());
        assertEquals("Histórico DTO", response.tituloObra());
        assertEquals(midia.getId(), response.midiaId());
        assertEquals(450, response.tempoAssistidoSegundos());
        assertTrue(response.concluido());
        assertNotNull(response.dataVisualizacao());

        List<HistoricoVisualizacaoResponse> lista = service.listarPorUsuario(usuario.getId());
        assertEquals(1, lista.size());
        assertEquals(response.id(), lista.get(0).id());
    }

    @Test
    void deveRejeitarRequestNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> service.registrar(UUID.randomUUID(), null));
    }

    @Test
    void deveListarMaisRecentePrimeiro() {
        Usuario usuario = salvarUsuario("listar-historico@teste.com");
        ObraAudiovisual obra = salvarObra("Listar histórico");
        MidiaStreaming midia = salvarMidia(obra, TipoMidia.PRINCIPAL);
        service.registrar(usuario.getId(), new HistoricoVisualizacaoRequest(midia.getId(), 10, false));
        HistoricoVisualizacaoResponse recente = service.registrar(
                usuario.getId(), new HistoricoVisualizacaoRequest(midia.getId(), 20, false));

        var resultado = service.listarPorUsuario(usuario.getId());
        assertEquals(2, resultado.size());
        assertEquals(recente.id(), resultado.get(0).id());
        assertEquals(recente.id(), service.listarEntidadesPorUsuario(usuario.getId()).get(0).getId());
    }
}
