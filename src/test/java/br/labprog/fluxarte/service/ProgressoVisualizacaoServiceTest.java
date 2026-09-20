package br.labprog.fluxarte.service;

import br.labprog.fluxarte.dto.request.ProgressoVisualizacaoRequest;
import br.labprog.fluxarte.dto.response.ProgressoVisualizacaoResponse;
import br.labprog.fluxarte.model.MidiaStreaming;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.ProgressoVisualizacao;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.model.enums.TipoMidia;
import br.labprog.fluxarte.repository.ProgressoVisualizacaoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

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
    void deveSalvarProgressoViaDto() {
        Usuario usuario = salvarUsuario("progresso-dto@teste.com");
        ObraAudiovisual obra = salvarObra("Progresso DTO");
        MidiaStreaming midia = salvarMidia(obra, TipoMidia.PRINCIPAL);

        ProgressoVisualizacaoRequest request = new ProgressoVisualizacaoRequest(midia.getId(), 600);
        Optional<ProgressoVisualizacaoResponse> response = service.salvarProgresso(usuario.getId(), request);

        assertTrue(response.isPresent());
        assertEquals(obra.getId(), response.get().obraId());
        assertEquals(midia.getId(), response.get().midiaId());
        assertEquals(600, response.get().posicaoSegundos());
        assertEquals(1200, response.get().duracaoTotalSegundos());
        assertEquals(50.0, response.get().percentualAssistido(), 0.01);
    }

    @Test
    void deveExpurgarProgressoQuando100PorcentoAssistido() {
        Usuario usuario = salvarUsuario("progresso-concluido@teste.com");
        ObraAudiovisual obra = salvarObra("Progresso Concluido");
        MidiaStreaming midia = salvarMidia(obra, TipoMidia.PRINCIPAL);

        service.salvarProgresso(usuario.getId(), midia.getId(), 500);
        assertTrue(service.buscarProgresso(usuario.getId(), midia.getId()).isPresent());

        ProgressoVisualizacao concluido = service.salvarProgresso(usuario.getId(), midia.getId(), 1200);
        assertNull(concluido);
        assertTrue(service.buscarProgresso(usuario.getId(), midia.getId()).isEmpty());

        ProgressoVisualizacaoRequest requestConclusao = new ProgressoVisualizacaoRequest(midia.getId(), 1200);
        Optional<ProgressoVisualizacaoResponse> responseConclusao = service.salvarProgresso(usuario.getId(), requestConclusao);
        assertTrue(responseConclusao.isEmpty());
        assertTrue(service.buscarProgressoResponse(usuario.getId(), midia.getId()).isEmpty());
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
