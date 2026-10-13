package br.labprog.fluxarte.service;

import br.labprog.fluxarte.dto.request.EventoRequest;
import br.labprog.fluxarte.dto.response.EventoResponse;
import br.labprog.fluxarte.repository.EventoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.labprog.fluxarte.exceptions.RecursoNaoEncontradoException;
import br.labprog.fluxarte.exceptions.RegraNegocioException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EventoServiceTest extends AbstractServiceTest {

    @Autowired EventoService service;
    @Autowired EventoRepository repository;

    @Test
    void deveCadastrarAtualizarEConsultarEvento() {
        EventoRequest request = new EventoRequest(
                "Mostra Internacional",
                "Mostra de cinema independente",
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(10),
                "https://cdn.test/mostra.jpg"
        );

        EventoResponse criado = service.cadastrar(request);

        assertNotNull(criado.id());
        assertEquals("Mostra Internacional", criado.nome());
        assertTrue(criado.emAndamento());

        EventoRequest atualizacao = new EventoRequest(
                "Mostra Internacional 2026",
                "Descricao atualizada",
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(12),
                "https://cdn.test/mostra-nova.jpg"
        );

        EventoResponse atualizado = service.atualizar(criado.id(), atualizacao);

        assertEquals("Mostra Internacional 2026", atualizado.nome());
        assertEquals("Descricao atualizada", atualizado.descricao());
        assertEquals(1, service.listarTodos().size());
        assertEquals(1, service.buscarPorNome("Internacional").size());
        assertEquals(1, service.listarEmAndamento().size());
        assertEquals(criado.id(), service.buscarPorId(criado.id()).id());
        assertEquals(criado.id(), service.buscarEntidadePorId(criado.id()).getId());
    }

    @Test
    void deveRejeitarRequestNulo() {
        assertThrows(RegraNegocioException.class, () -> service.cadastrar(null));
        assertThrows(RegraNegocioException.class, () -> service.atualizar(1L, null));
    }

    @Test
    void deveInformarAusenciaEExcluir() {
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(Long.MAX_VALUE));
        EventoRequest request = new EventoRequest(
                "Excluir Evento",
                "Descricao",
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                null
        );
        EventoResponse salvo = service.cadastrar(request);
        service.excluir(salvo.id());
        repository.flush();
        assertFalse(repository.existsById(salvo.id()));
    }
}
