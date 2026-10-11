package br.labprog.fluxarte.service;

import br.labprog.fluxarte.dto.request.ProgressoVisualizacaoRequest;
import br.labprog.fluxarte.dto.response.ProgressoVisualizacaoResponse;
import br.labprog.fluxarte.exceptions.RegraNegocioException;
import br.labprog.fluxarte.model.MidiaStreaming;
import br.labprog.fluxarte.model.ObraAudiovisual;
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

        Optional<ProgressoVisualizacaoResponse> criado = service.salvarProgresso(
                usuario.getId(), new ProgressoVisualizacaoRequest(midia.getId(), 0));
        assertTrue(criado.isPresent());

        Optional<ProgressoVisualizacaoResponse> atualizado = service.salvarProgresso(
                usuario.getId(), new ProgressoVisualizacaoRequest(midia.getId(), 300));
        assertTrue(atualizado.isPresent());

        assertEquals(criado.get().id(), atualizado.get().id());
        assertEquals(1, repository.count());
        assertEquals(300, atualizado.get().posicaoSegundos());
        assertEquals(obra.getId(), atualizado.get().obraId());
        assertEquals(midia.getId(), atualizado.get().midiaId());
        assertEquals(25.0, atualizado.get().percentualAssistido(), 0.01);
    }

    @Test
    void deveExpurgarProgressoQuando100PorcentoAssistido() {
        Usuario usuario = salvarUsuario("progresso-concluido@teste.com");
        ObraAudiovisual obra = salvarObra("Progresso Concluido");
        MidiaStreaming midia = salvarMidia(obra, TipoMidia.PRINCIPAL);

        service.salvarProgresso(usuario.getId(), new ProgressoVisualizacaoRequest(midia.getId(), 500));
        assertTrue(service.buscarProgresso(usuario.getId(), midia.getId()).isPresent());

        Optional<ProgressoVisualizacaoResponse> concluido = service.salvarProgresso(
                usuario.getId(), new ProgressoVisualizacaoRequest(midia.getId(), 1200));
        assertTrue(concluido.isEmpty());
        assertTrue(service.buscarProgresso(usuario.getId(), midia.getId()).isEmpty());
    }

    @Test
    void deveValidarPosicaoContraDuracaoERequestNulo() {
        Usuario usuario = salvarUsuario("posicao-invalida@teste.com");
        ObraAudiovisual obra = salvarObra("Duração");
        MidiaStreaming midia = salvarMidia(obra, TipoMidia.PRINCIPAL);

        assertThrows(IllegalArgumentException.class,
                () -> service.salvarProgresso(usuario.getId(), null));
        assertEquals("Posicao nao pode ser maior que a duracao da midia",
                assertThrows(RegraNegocioException.class,
                        () -> service.salvarProgresso(usuario.getId(),
                                new ProgressoVisualizacaoRequest(midia.getId(), 1201))).getMessage());
    }

    @Test
    void deveConsultarListarERemoverDeFormaIdempotente() {
        Usuario usuario = salvarUsuario("consultar-progresso@teste.com");
        ObraAudiovisual obra = salvarObra("Consultar progresso");
        MidiaStreaming principal = salvarMidia(obra, TipoMidia.PRINCIPAL);
        MidiaStreaming trailer = salvarMidia(obra, TipoMidia.TRAILER);

        service.salvarProgresso(usuario.getId(), new ProgressoVisualizacaoRequest(principal.getId(), 10));
        Optional<ProgressoVisualizacaoResponse> recente = service.salvarProgresso(
                usuario.getId(), new ProgressoVisualizacaoRequest(trailer.getId(), 20));

        assertTrue(service.buscarProgresso(usuario.getId(), principal.getId()).isPresent());
        assertEquals(2, service.listarPorUsuario(usuario.getId()).size());
        assertEquals(recente.get().id(), service.listarPorUsuario(usuario.getId()).get(0).id());

        service.remover(usuario.getId(), principal.getId());
        service.remover(usuario.getId(), principal.getId());
        assertTrue(service.buscarProgresso(usuario.getId(), principal.getId()).isEmpty());
    }
}
