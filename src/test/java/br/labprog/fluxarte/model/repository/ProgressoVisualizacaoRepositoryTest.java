package br.labprog.fluxarte.model.repository;

import br.labprog.fluxarte.model.MidiaStreaming;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.ProgressoVisualizacao;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.model.enums.TipoMidia;
import br.labprog.fluxarte.repository.MidiaStreamingRepository;
import br.labprog.fluxarte.repository.ObraAudiovisualRepository;
import br.labprog.fluxarte.repository.ProgressoVisualizacaoRepository;
import br.labprog.fluxarte.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;

class ProgressoVisualizacaoRepositoryTest extends AbstractRepositoryTest {

    @Autowired ProgressoVisualizacaoRepository repository;
    @Autowired UsuarioRepository usuarioRepository;
    @Autowired ObraAudiovisualRepository obraRepository;
    @Autowired MidiaStreamingRepository midiaRepository;

    @Test
    void deveSalvarBuscarEOrdenarProgresso() {
        Usuario usuario = usuarioRepository.save(ModelFixtures.usuario("progresso@teste.com"));
        ObraAudiovisual obra = obraRepository.save(ModelFixtures.obra("Obra progresso"));
        MidiaStreaming principal = midiaRepository.save(ModelFixtures.midia(obra, TipoMidia.PRINCIPAL));
        MidiaStreaming trailer = midiaRepository.save(ModelFixtures.midia(obra, TipoMidia.TRAILER));
        ProgressoVisualizacao primeiro = repository.saveAndFlush(progresso(usuario, obra, principal, 100));
        ProgressoVisualizacao segundo = repository.saveAndFlush(progresso(usuario, obra, trailer, 50));
        flushAndClear();

        ProgressoVisualizacao recuperado = repository
                .findByUsuarioIdAndMidiaId(usuario.getId(), principal.getId()).orElseThrow();
        assertEquals(100, recuperado.getPosicaoSegundos());
        assertNotNull(recuperado.getAtualizadoEm());
        assertEquals(2, repository.findByUsuarioIdOrderByAtualizadoEmDesc(usuario.getId()).size());
        assertEquals(segundo.getId(), repository.findByUsuarioIdOrderByAtualizadoEmDesc(usuario.getId()).get(0).getId());
        assertNotNull(primeiro.getId());
    }

    @Test
    void naoDevePermitirProgressoDuplicadoParaUsuarioEMidia() {
        Usuario usuario = usuarioRepository.save(ModelFixtures.usuario("duplicado-progresso@teste.com"));
        ObraAudiovisual obra = obraRepository.save(ModelFixtures.obra("Obra duplicada"));
        MidiaStreaming midia = midiaRepository.save(ModelFixtures.midia(obra, TipoMidia.PRINCIPAL));
        repository.saveAndFlush(progresso(usuario, obra, midia, 10));

        assertThrows(DataIntegrityViolationException.class,
                () -> repository.saveAndFlush(progresso(usuario, obra, midia, 20)));
    }

    private ProgressoVisualizacao progresso(Usuario usuario, ObraAudiovisual obra,
                                             MidiaStreaming midia, int posicao) {
        return ProgressoVisualizacao.builder()
                .usuario(usuario)
                .obra(obra)
                .midia(midia)
                .posicaoSegundos(posicao)
                .build();
    }
}
