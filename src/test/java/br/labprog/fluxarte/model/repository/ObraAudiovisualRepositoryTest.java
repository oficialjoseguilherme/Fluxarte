package br.labprog.fluxarte.model.repository;

import br.labprog.fluxarte.model.GeneroObra;
import br.labprog.fluxarte.model.MidiaStreaming;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.enums.NomeGenero;
import br.labprog.fluxarte.model.enums.StatusObra;
import br.labprog.fluxarte.model.enums.TipoMidia;
import br.labprog.fluxarte.repository.GeneroObraRepository;
import br.labprog.fluxarte.repository.MidiaStreamingRepository;
import br.labprog.fluxarte.repository.ObraAudiovisualRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ObraAudiovisualRepositoryTest extends AbstractRepositoryTest {

    @Autowired ObraAudiovisualRepository repository;
    @Autowired GeneroObraRepository generoRepository;
    @Autowired MidiaStreamingRepository midiaRepository;

    @Test
    void deveSalvarTodosOsCamposEGenero() {
        GeneroObra genero = generoRepository.save(ModelFixtures.genero(NomeGenero.DOCUMENTARIO));
        ObraAudiovisual obra = ModelFixtures.obra("Cinema Maranhense");
        obra.getGeneros().add(genero);

        Long id = repository.saveAndFlush(obra).getId();
        flushAndClear();

        ObraAudiovisual recuperada = repository.findById(id).orElseThrow();
        assertEquals("Cinema Maranhense", recuperada.getTitulo());
        assertEquals("Cinema Maranhense Original", recuperada.getTituloOriginal());
        assertEquals(StatusObra.ATIVO, recuperada.getStatus());
        assertEquals(1, recuperada.getGeneros().size());
        assertNotNull(recuperada.getCriadoEm());
        assertNotNull(recuperada.getAtualizadoEm());
    }

    @Test
    void deveExecutarConsultasDeCatalogoEJanelaDeExibicao() {
        LocalDateTime agora = LocalDateTime.of(2026, 9, 18, 12, 0);
        ObraAudiovisual exibivel = ModelFixtures.obra("Filme Exibível");
        exibivel.setDiretor("Ana Silva");
        exibivel.setDataInicioExibicao(agora.minusDays(1));
        exibivel.setDataFimExibicao(agora.plusDays(1));
        ObraAudiovisual futura = ModelFixtures.obra("Filme Futuro");
        futura.setDataInicioExibicao(agora.plusDays(1));
        repository.saveAllAndFlush(java.util.List.of(exibivel, futura));

        assertEquals(1, repository.findByTituloContainingIgnoreCase("exibível").size());
        assertEquals(1, repository.findByDiretorContainingIgnoreCase("ana").size());
        assertEquals(2, repository.findByStatus(StatusObra.ATIVO).size());
        assertEquals(1, repository.findExibiveis(agora, StatusObra.ATIVO).size());
    }

    @Test
    void deveAplicarCascataEOrphanRemovalNasMidias() {
        ObraAudiovisual obra = ModelFixtures.obra("Obra com mídia");
        MidiaStreaming midia = ModelFixtures.midia(obra, TipoMidia.PRINCIPAL);
        obra.getMidias().add(midia);
        Long obraId = repository.saveAndFlush(obra).getId();
        Long midiaId = midia.getId();
        assertNotNull(midiaId);

        obra.getMidias().remove(midia);
        repository.saveAndFlush(obra);
        entityManager.clear();

        assertTrue(repository.findById(obraId).isPresent());
        assertFalse(midiaRepository.findById(midiaId).isPresent());
    }
}
