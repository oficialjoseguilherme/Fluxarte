package br.labprog.fluxarte.service;

import br.labprog.fluxarte.model.MidiaStreaming;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.enums.TipoMidia;
import br.labprog.fluxarte.model.enums.TipoPlayer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class MidiaStreamingServiceTest extends AbstractServiceTest {

    @Autowired MidiaStreamingService service;

    @Test
    void deveCadastrarAtualizarEConsultarMidia() {
        ObraAudiovisual obra = salvarObra("Obra mídia");
        MidiaStreaming salva = service.cadastrar(obra.getId(),
                ServiceFixtures.midia(null, TipoMidia.PRINCIPAL));
        MidiaStreaming novosDados = ServiceFixtures.midia(null, TipoMidia.TRAILER);
        novosDados.setDuracaoSegundos(90);

        MidiaStreaming atualizada = service.atualizar(salva.getId(), novosDados);

        assertEquals(obra.getId(), atualizada.getObra().getId());
        assertEquals(TipoMidia.TRAILER, atualizada.getTipoMidia());
        assertEquals(90, atualizada.getDuracaoSegundos());
        assertNotNull(atualizada.getAtualizadoEm());
        assertEquals(atualizada.getId(), service.buscarPorId(atualizada.getId()).getId());
        assertEquals(1, service.listarPorObra(obra.getId()).size());
        assertEquals(1, service.listarPorObraETipo(obra.getId(), TipoMidia.TRAILER).size());
    }

    @Test
    void deveValidarTipoDuracaoEUrlsDosPlayers() {
        ObraAudiovisual obra = salvarObra("Validação mídia");
        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(obra.getId(), null));
        MidiaStreaming semTipo = ServiceFixtures.midia(null, TipoMidia.PRINCIPAL);
        semTipo.setTipoMidia(null);
        assertEquals("Tipo da midia e obrigatorio",
                assertThrows(IllegalArgumentException.class,
                        () -> service.cadastrar(obra.getId(), semTipo)).getMessage());
        MidiaStreaming duracao = ServiceFixtures.midia(null, TipoMidia.PRINCIPAL);
        duracao.setDuracaoSegundos(0);
        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(obra.getId(), duracao));
        MidiaStreaming embed = ServiceFixtures.midia(null, TipoMidia.PRINCIPAL);
        embed.setPlayerType(TipoPlayer.EMBED);
        embed.setEduplayEmbedUrl(" ");
        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(obra.getId(), embed));
        MidiaStreaming nativePlayer = ServiceFixtures.midia(null, TipoMidia.PRINCIPAL);
        nativePlayer.setPlayerType(TipoPlayer.NATIVE);
        nativePlayer.setCdnStreamUrl(null);
        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(obra.getId(), nativePlayer));
    }

    @Test
    void deveRejeitarObraOuMidiaInexistente() {
        assertThrows(NoSuchElementException.class,
                () -> service.cadastrar(Long.MAX_VALUE, ServiceFixtures.midia(null, TipoMidia.PRINCIPAL)));
        assertThrows(NoSuchElementException.class, () -> service.buscarPorId(Long.MAX_VALUE));
    }

    @Test
    void deveExcluirMidia() {
        ObraAudiovisual obra = salvarObra("Excluir mídia");
        MidiaStreaming midia = salvarMidia(obra, TipoMidia.PRINCIPAL);
        service.excluir(midia.getId());
        midiaRepository.flush();
        assertFalse(midiaRepository.existsById(midia.getId()));
    }
}
