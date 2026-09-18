package br.labprog.fluxarte.model.repository;

import br.labprog.fluxarte.model.Evento;
import br.labprog.fluxarte.model.EventoObra;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.enums.CategoriaEvento;
import br.labprog.fluxarte.repository.EventoObraRepository;
import br.labprog.fluxarte.repository.EventoRepository;
import br.labprog.fluxarte.repository.ObraAudiovisualRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EventoObraRepositoryTest extends AbstractRepositoryTest {

    @Autowired EventoObraRepository repository;
    @Autowired EventoRepository eventoRepository;
    @Autowired ObraAudiovisualRepository obraRepository;

    @Test
    void deveSalvarRelacionamentosEExecutarConsultas() {
        Evento evento = eventoRepository.save(ModelFixtures.evento("Mostra"));
        ObraAudiovisual obra = obraRepository.save(ModelFixtures.obra("Obra da mostra"));
        EventoObra vinculo = novoVinculo(evento, obra, true);

        Long id = repository.saveAndFlush(vinculo).getId();
        flushAndClear();

        EventoObra recuperado = repository.findById(id).orElseThrow();
        assertEquals(CategoriaEvento.COMPETICAO, recuperado.getCategoria());
        assertEquals("Melhor Filme", recuperado.getPremiacao());
        assertTrue(repository.existsByEventoIdAndObraId(evento.getId(), obra.getId()));
        assertEquals(1, repository.findByEventoId(evento.getId()).size());
        assertEquals(1, repository.findByObraId(obra.getId()).size());
        assertEquals(1, repository.findByEventoIdAndDestaqueTrue(evento.getId()).size());
    }

    @Test
    void naoDevePermitirMesmoParEventoObra() {
        Evento evento = eventoRepository.save(ModelFixtures.evento("Evento único"));
        ObraAudiovisual obra = obraRepository.save(ModelFixtures.obra("Obra única"));
        repository.saveAndFlush(novoVinculo(evento, obra, false));

        assertThrows(DataIntegrityViolationException.class,
                () -> repository.saveAndFlush(novoVinculo(evento, obra, true)));
    }

    private EventoObra novoVinculo(Evento evento, ObraAudiovisual obra, boolean destaque) {
        return EventoObra.builder()
                .evento(evento)
                .obra(obra)
                .categoria(CategoriaEvento.COMPETICAO)
                .dataEstreiaEventoInicio(LocalDateTime.of(2026, 9, 10, 10, 0))
                .dataEstreiaEventoFim(LocalDateTime.of(2026, 9, 20, 22, 0))
                .premiacao("Melhor Filme")
                .destaque(destaque)
                .build();
    }
}
