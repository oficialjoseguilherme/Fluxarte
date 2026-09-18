package br.labprog.fluxarte.model.repository;

import br.labprog.fluxarte.model.HistoricoVisualizacao;
import br.labprog.fluxarte.model.MidiaStreaming;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.model.enums.TipoMidia;
import br.labprog.fluxarte.repository.HistoricoVisualizacaoRepository;
import br.labprog.fluxarte.repository.MidiaStreamingRepository;
import br.labprog.fluxarte.repository.ObraAudiovisualRepository;
import br.labprog.fluxarte.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HistoricoVisualizacaoRepositoryTest extends AbstractRepositoryTest {

    @Autowired HistoricoVisualizacaoRepository repository;
    @Autowired UsuarioRepository usuarioRepository;
    @Autowired ObraAudiovisualRepository obraRepository;
    @Autowired MidiaStreamingRepository midiaRepository;

    @Test
    void deveSalvarCamposERetornarHistoricoMaisRecentePrimeiro() {
        Usuario usuario = usuarioRepository.save(ModelFixtures.usuario("historico@teste.com"));
        ObraAudiovisual obra = obraRepository.save(ModelFixtures.obra("Obra histórica"));
        MidiaStreaming midia = midiaRepository.save(ModelFixtures.midia(obra, TipoMidia.PRINCIPAL));
        HistoricoVisualizacao antigo = historico(usuario, obra, midia,
                LocalDateTime.of(2026, 9, 10, 10, 0), false);
        HistoricoVisualizacao recente = historico(usuario, obra, midia,
                LocalDateTime.of(2026, 9, 18, 10, 0), true);
        repository.saveAllAndFlush(java.util.List.of(antigo, recente));
        flushAndClear();

        var historicos = repository.findByUsuarioIdOrderByDataVisualizacaoDesc(usuario.getId());
        assertEquals(2, historicos.size());
        assertEquals(recente.getId(), historicos.get(0).getId());
        assertTrue(historicos.get(0).getConcluido());
        assertEquals(600, historicos.get(0).getTempoAssistidoSegundos());
        assertEquals(midia.getId(), historicos.get(0).getMidia().getId());
    }

    private HistoricoVisualizacao historico(Usuario usuario, ObraAudiovisual obra,
                                             MidiaStreaming midia, LocalDateTime data,
                                             boolean concluido) {
        return HistoricoVisualizacao.builder()
                .usuario(usuario)
                .obra(obra)
                .midia(midia)
                .dataVisualizacao(data)
                .tempoAssistidoSegundos(600)
                .concluido(concluido)
                .build();
    }
}
