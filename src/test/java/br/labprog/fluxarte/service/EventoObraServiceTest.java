package br.labprog.fluxarte.service;

import br.labprog.fluxarte.dto.request.EventoObraRequest;
import br.labprog.fluxarte.dto.response.EventoObraResponse;
import br.labprog.fluxarte.model.Evento;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.enums.CategoriaEvento;
import br.labprog.fluxarte.repository.EventoObraRepository;
import br.labprog.fluxarte.repository.EventoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.labprog.fluxarte.exceptions.ConflitoException;
import br.labprog.fluxarte.exceptions.RecursoNaoEncontradoException;
import br.labprog.fluxarte.exceptions.RegraNegocioException;

import static org.junit.jupiter.api.Assertions.*;

class EventoObraServiceTest extends AbstractServiceTest {

    @Autowired EventoObraService service;
    @Autowired EventoRepository eventoRepository;
    @Autowired EventoObraRepository repository;

    @Test
    void deveAdicionarAtualizarEConsultarObraNoEvento() {
        Evento evento = eventoRepository.saveAndFlush(ServiceFixtures.evento("Mostra"));
        ObraAudiovisual obra = salvarObra("Obra da mostra");
        EventoObraRequest request = new EventoObraRequest(
                obra.getId(),
                CategoriaEvento.COMPETICAO,
                evento.getDataInicio().atStartOfDay(),
                evento.getDataFim().atTime(23, 59),
                "Premio",
                null
        );
        EventoObraResponse salvo = service.adicionarObra(evento.getId(), request);
        EventoObraRequest atualizacao = new EventoObraRequest(
                obra.getId(),
                CategoriaEvento.HOMENAGEM,
                evento.getDataInicio().atStartOfDay(),
                evento.getDataFim().atTime(23, 59),
                "Premio",
                true
        );

        EventoObraResponse atualizado = service.atualizar(salvo.id(), atualizacao);

        assertEquals(evento.getId(), service.buscarEntidadePorId(atualizado.id()).getEvento().getId());
        assertEquals(obra.getId(), atualizado.obraId());
        assertEquals(CategoriaEvento.HOMENAGEM, atualizado.categoria());
        assertTrue(atualizado.destaque());
        assertEquals(1, service.listarPorEvento(evento.getId()).size());
        assertEquals(1, service.listarPorObra(obra.getId()).size());
        assertEquals(1, service.listarDestaquesDoEvento(evento.getId()).size());
    }

    @Test
    void deveValidarDatasEstreiaDentroDoPeriodoDoEvento() {
        Evento evento = eventoRepository.saveAndFlush(ServiceFixtures.evento("Festival Restrito"));
        ObraAudiovisual obra = salvarObra("Obra Restrita");

        EventoObraRequest antesDoEvento = new EventoObraRequest(
                obra.getId(),
                CategoriaEvento.COMPETICAO,
                evento.getDataInicio().minusDays(10).atStartOfDay(),
                evento.getDataFim().atTime(23, 59),
                null,
                false
        );

        assertEquals("Data de inicio da estreia nao pode ser anterior ao inicio do evento",
                assertThrows(RegraNegocioException.class,
                        () -> service.adicionarObra(evento.getId(), antesDoEvento)).getMessage());

        EventoObraRequest depoisDoEvento = new EventoObraRequest(
                obra.getId(),
                CategoriaEvento.COMPETICAO,
                evento.getDataInicio().atStartOfDay(),
                evento.getDataFim().plusDays(20).atTime(23, 59),
                null,
                false
        );

        assertEquals("Data de fim da estreia nao pode ser posterior ao fim do evento",
                assertThrows(RegraNegocioException.class,
                        () -> service.adicionarObra(evento.getId(), depoisDoEvento)).getMessage());
    }

    @Test
    void deveAplicarDestaqueFalsoComoPadrao() {
        Evento evento = eventoRepository.saveAndFlush(ServiceFixtures.evento("Padrão"));
        ObraAudiovisual obra = salvarObra("Obra padrão");
        EventoObraRequest request = new EventoObraRequest(
                obra.getId(),
                CategoriaEvento.MOSTRA,
                evento.getDataInicio().atStartOfDay(),
                evento.getDataFim().atTime(23, 59),
                null,
                null
        );
        assertFalse(service.adicionarObra(evento.getId(), request).destaque());
    }

    @Test
    void deveRejeitarVinculoDuplicadoEDadosInvalidos() {
        Evento evento = eventoRepository.saveAndFlush(ServiceFixtures.evento("Duplicado"));
        ObraAudiovisual obra = salvarObra("Obra duplicada");
        EventoObraRequest request = new EventoObraRequest(
                obra.getId(),
                CategoriaEvento.MOSTRA,
                evento.getDataInicio().atStartOfDay(),
                evento.getDataFim().atTime(23, 59),
                null,
                false
        );
        service.adicionarObra(evento.getId(), request);
        assertEquals("Essa obra ja faz parte da programacao do evento",
                assertThrows(ConflitoException.class,
                        () -> service.adicionarObra(evento.getId(), request)).getMessage());
        assertThrows(IllegalArgumentException.class,
                () -> service.adicionarObra(evento.getId(), null));
        assertThrows(IllegalArgumentException.class,
                () -> service.atualizar(1L, null));
    }

    @Test
    void deveInformarAusenciaERemover() {
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(Long.MAX_VALUE));
        Evento evento = eventoRepository.saveAndFlush(ServiceFixtures.evento("Remover"));
        ObraAudiovisual obra = salvarObra("Remover vínculo");
        EventoObraRequest request = new EventoObraRequest(
                obra.getId(),
                CategoriaEvento.MOSTRA,
                evento.getDataInicio().atStartOfDay(),
                evento.getDataFim().atTime(23, 59),
                null,
                false
        );
        EventoObraResponse salvo = service.adicionarObra(evento.getId(), request);
        service.remover(salvo.id());
        repository.flush();
        assertFalse(repository.existsById(salvo.id()));
    }
}
