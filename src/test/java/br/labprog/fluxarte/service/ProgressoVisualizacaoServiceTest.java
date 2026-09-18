package br.labprog.fluxarte.service;

import br.labprog.fluxarte.model.MidiaStreaming;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.ProgressoVisualizacao;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.model.enums.TipoMidia;
import br.labprog.fluxarte.repository.ProgressoVisualizacaoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class ProgressoVisualizacaoServiceTest extends AbstractServiceTest {

    @Autowired ProgressoVisualizacaoService service;
    @Autowired ProgressoVisualizacaoRepository repository;

    @Test
    void deveCriarEAtualizarOMesmoProgresso() {
        Usuario usuario = salvarUsuario("progresso-service@teste.com");
        ObraAudiovisual obra = salvarObra("Progresso service");
        MidiaStreaming midia = salvarMidia(obra, TipoMidia.PRINCIPAL);

        ProgressoVisualizacao criado = service.salvarProgresso(usuario.getId(), midia.getId(), 0);
        ProgressoVisualizacao atualizado = service.salvarProgresso(usuario.getId(), midia.getId(), 300);

        assertEquals(criado.getId(), atualizado.getId());
        assertEquals(1, repository.count());
        assertEquals(300, atualizado.getPosicaoSegundos());
        assertEquals(obra.getId(), atualizado.getObra().getId());
        assertEquals(midia.getId(), atualizado.getMidia().getId());
        assertNotNull(atualizado.getAtualizadoEm());
    }

    @Test
    void deveValidarPosicaoContraDuracao() {
        Usuario usuario = salvarUsuario("posicao-invalida@teste.com");
        ObraAudiovisual obra = salvarObra("Duração");
        MidiaStreaming midia = salvarMidia(obra, TipoMidia.PRINCIPAL);

        assertThrows(IllegalArgumentException.class,
                () -> service.salvarProgresso(usuario.getId(), midia.getId(), null));
        assertThrows(IllegalArgumentException.class,
                () -> service.salvarProgresso(usuario.getId(), midia.getId(), -1));
        assertEquals("Posicao nao pode ser maior que a duracao da midia",
                assertThrows(IllegalArgumentException.class,
                        () -> service.salvarProgresso(usuario.getId(), midia.getId(), 1201)).getMessage());
    }

    @Test
    void deveConsultarListarERemoverDeFormaIdempotente() {
        Usuario usuario = salvarUsuario("consultar-progresso@teste.com");
        ObraAudiovisual obra = salvarObra("Consultar progresso");
        MidiaStreaming principal = salvarMidia(obra, TipoMidia.PRINCIPAL);
        MidiaStreaming trailer = salvarMidia(obra, TipoMidia.TRAILER);
        service.salvarProgresso(usuario.getId(), principal.getId(), 10);
        ProgressoVisualizacao recente = service.salvarProgresso(usuario.getId(), trailer.getId(), 20);

        assertTrue(service.buscarProgresso(usuario.getId(), principal.getId()).isPresent());
        assertEquals(2, service.listarPorUsuario(usuario.getId()).size());
        assertEquals(recente.getId(), service.listarPorUsuario(usuario.getId()).get(0).getId());

        service.remover(usuario.getId(), principal.getId());
        service.remover(usuario.getId(), principal.getId());
        assertTrue(service.buscarProgresso(usuario.getId(), principal.getId()).isEmpty());
    }
}
