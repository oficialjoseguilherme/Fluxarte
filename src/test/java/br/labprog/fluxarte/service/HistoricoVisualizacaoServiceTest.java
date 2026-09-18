package br.labprog.fluxarte.service;

import br.labprog.fluxarte.model.HistoricoVisualizacao;
import br.labprog.fluxarte.model.MidiaStreaming;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.model.enums.TipoMidia;
import br.labprog.fluxarte.repository.HistoricoVisualizacaoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class HistoricoVisualizacaoServiceTest extends AbstractServiceTest {

    @Autowired HistoricoVisualizacaoService service;
    @Autowired HistoricoVisualizacaoRepository repository;

    @Test
    void deveRegistrarUmaLinhaPorSessaoEAssociarObraDaMidia() {
        Usuario usuario = salvarUsuario("historico-service@teste.com");
        ObraAudiovisual obra = salvarObra("Histórico service");
        MidiaStreaming midia = salvarMidia(obra, TipoMidia.PRINCIPAL);

        HistoricoVisualizacao primeiro = service.registrar(usuario.getId(), midia.getId(), 120, null);
        HistoricoVisualizacao segundo = service.registrar(usuario.getId(), midia.getId(), 600, true);

        assertNotEquals(primeiro.getId(), segundo.getId());
        assertEquals(2, repository.count());
        assertEquals(obra.getId(), segundo.getObra().getId());
        assertEquals(midia.getId(), segundo.getMidia().getId());
        assertFalse(primeiro.getConcluido());
        assertTrue(segundo.getConcluido());
        assertNotNull(segundo.getDataVisualizacao());
    }

    @Test
    void deveRejeitarTempoNegativo() {
        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                () -> service.registrar(java.util.UUID.randomUUID(), 1L, -1, false));
        assertEquals("Tempo assistido nao pode ser negativo", erro.getMessage());
    }

    @Test
    void deveListarMaisRecentePrimeiro() {
        Usuario usuario = salvarUsuario("listar-historico@teste.com");
        ObraAudiovisual obra = salvarObra("Listar histórico");
        MidiaStreaming midia = salvarMidia(obra, TipoMidia.PRINCIPAL);
        service.registrar(usuario.getId(), midia.getId(), 10, false);
        HistoricoVisualizacao recente = service.registrar(usuario.getId(), midia.getId(), 20, false);

        var resultado = service.listarPorUsuario(usuario.getId());
        assertEquals(2, resultado.size());
        assertEquals(recente.getId(), resultado.get(0).getId());
    }
}
