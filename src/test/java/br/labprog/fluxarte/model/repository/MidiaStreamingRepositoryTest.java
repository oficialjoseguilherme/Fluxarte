package br.labprog.fluxarte.model.repository;

import br.labprog.fluxarte.model.MidiaStreaming;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.enums.FormatoMidia;
import br.labprog.fluxarte.model.enums.Resolucao;
import br.labprog.fluxarte.model.enums.TipoMidia;
import br.labprog.fluxarte.model.enums.TipoPlayer;
import br.labprog.fluxarte.repository.MidiaStreamingRepository;
import br.labprog.fluxarte.repository.ObraAudiovisualRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;

class MidiaStreamingRepositoryTest extends AbstractRepositoryTest {

    @Autowired MidiaStreamingRepository repository;
    @Autowired ObraAudiovisualRepository obraRepository;

    @Test
    void deveSalvarMapeamentoEConsultarPorObraETipo() {
        ObraAudiovisual obra = obraRepository.save(ModelFixtures.obra("Obra da mídia"));
        MidiaStreaming midia = ModelFixtures.midia(obra, TipoMidia.TRAILER);

        Long id = repository.saveAndFlush(midia).getId();
        flushAndClear();

        MidiaStreaming recuperada = repository.findById(id).orElseThrow();
        assertEquals(obra.getId(), recuperada.getObra().getId());
        assertEquals(TipoMidia.TRAILER, recuperada.getTipoMidia());
        assertEquals(Resolucao.R1080P, recuperada.getResolucao());
        assertEquals(FormatoMidia.HLS, recuperada.getFormato());
        assertEquals(TipoPlayer.NATIVE, recuperada.getPlayerType());
        assertEquals(1200, recuperada.getDuracaoSegundos());
        assertNotNull(recuperada.getAtualizadoEm());
        assertEquals(1, repository.findByObraId(obra.getId()).size());
        assertEquals(1, repository.findByObraIdAndTipoMidia(obra.getId(), TipoMidia.TRAILER).size());
    }

    @Test
    void naoDeveSalvarSemObra() {
        MidiaStreaming midia = ModelFixtures.midia(null, TipoMidia.PRINCIPAL);

        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(midia));
    }
}
