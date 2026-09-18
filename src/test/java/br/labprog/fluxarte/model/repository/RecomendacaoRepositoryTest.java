package br.labprog.fluxarte.model.repository;

import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.Recomendacao;
import br.labprog.fluxarte.model.Usuario;
import br.labprog.fluxarte.model.enums.AlgoritmoRecomendacao;
import br.labprog.fluxarte.repository.ObraAudiovisualRepository;
import br.labprog.fluxarte.repository.RecomendacaoRepository;
import br.labprog.fluxarte.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class RecomendacaoRepositoryTest extends AbstractRepositoryTest {

    @Autowired RecomendacaoRepository repository;
    @Autowired UsuarioRepository usuarioRepository;
    @Autowired ObraAudiovisualRepository obraRepository;

    @Test
    void deveOrdenarPorScoreEPersistirAlgoritmo() {
        Usuario usuario = usuarioRepository.save(ModelFixtures.usuario("recomendacao@teste.com"));
        ObraAudiovisual obra1 = obraRepository.save(ModelFixtures.obra("Recomendação 1"));
        ObraAudiovisual obra2 = obraRepository.save(ModelFixtures.obra("Recomendação 2"));
        repository.saveAllAndFlush(java.util.List.of(
                recomendacao(usuario, obra1, 0.25f),
                recomendacao(usuario, obra2, 0.90f)));
        flushAndClear();

        var recomendacoes = repository.findByUsuarioIdOrderByScoreDesc(usuario.getId());
        assertEquals(2, recomendacoes.size());
        assertEquals(0.90f, recomendacoes.get(0).getScore());
        assertEquals(AlgoritmoRecomendacao.PREFERENCIA_GENERO, recomendacoes.get(0).getAlgoritmo());
        assertNotNull(recomendacoes.get(0).getGeradaEm());
    }

    @Test
    void deveExcluirTodasAsRecomendacoesDoUsuario() {
        Usuario usuario = usuarioRepository.save(ModelFixtures.usuario("excluir-recomendacao@teste.com"));
        ObraAudiovisual obra = obraRepository.save(ModelFixtures.obra("Obra recomendada"));
        repository.saveAndFlush(recomendacao(usuario, obra, 0.8f));

        repository.deleteByUsuarioId(usuario.getId());
        repository.flush();

        assertTrue(repository.findByUsuarioIdOrderByScoreDesc(usuario.getId()).isEmpty());
    }

    private Recomendacao recomendacao(Usuario usuario, ObraAudiovisual obra, float score) {
        return Recomendacao.builder()
                .usuario(usuario)
                .obra(obra)
                .algoritmo(AlgoritmoRecomendacao.PREFERENCIA_GENERO)
                .score(score)
                .build();
    }
}
