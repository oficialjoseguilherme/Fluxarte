package br.labprog.fluxarte.model.repository;

import br.labprog.fluxarte.model.Evento;
import br.labprog.fluxarte.repository.EventoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventoRepositoryTest extends AbstractRepositoryTest {

    @Autowired EventoRepository repository;

    @Test
    void deveSalvarBuscarPorNomeERemover() {
        Evento evento = repository.saveAndFlush(ModelFixtures.evento("Festival Guarnice"));
        Long id = evento.getId();
        flushAndClear();

        assertEquals(1, repository.findByNomeContainingIgnoreCase("guarnice").size());
        assertEquals("Evento de teste", repository.findById(id).orElseThrow().getDescricao());

        repository.deleteById(id);
        repository.flush();
        assertFalse(repository.findById(id).isPresent());
    }

    @Test
    void deveEncontrarEventosEmAndamentoComLimitesInclusivosENulos() {
        LocalDate data = LocalDate.of(2026, 9, 18);
        Evento limitado = ModelFixtures.evento("Limitado");
        limitado.setDataInicio(data);
        limitado.setDataFim(data);
        Evento semLimites = ModelFixtures.evento("Permanente");
        semLimites.setDataInicio(null);
        semLimites.setDataFim(null);
        Evento encerrado = ModelFixtures.evento("Encerrado");
        encerrado.setDataFim(data.minusDays(1));
        repository.saveAllAndFlush(List.of(limitado, semLimites, encerrado));

        assertEquals(2, repository.findEmAndamento(data).size());
    }
}
