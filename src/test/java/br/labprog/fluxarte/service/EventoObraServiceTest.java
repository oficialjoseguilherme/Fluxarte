package br.labprog.fluxarte.service;

import br.labprog.fluxarte.model.Evento;
import br.labprog.fluxarte.model.EventoObra;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.enums.CategoriaEvento;
import br.labprog.fluxarte.repository.EventoObraRepository;
import br.labprog.fluxarte.repository.EventoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class EventoObraServiceTest extends AbstractServiceTest {

    @Autowired EventoObraService service;
    @Autowired EventoRepository eventoRepository;
    @Autowired EventoObraRepository repository;

    @Test
    void deveAdicionarAtualizarEConsultarObraNoEvento() {
        Evento evento = eventoRepository.saveAndFlush(ServiceFixtures.evento("Mostra"));
        ObraAudiovisual obra = salvarObra("Obra da mostra");
        EventoObra dados = ServiceFixtures.eventoObra();
        dados.setDestaque(null);
        EventoObra salvo = service.adicionarObra(evento.getId(), obra.getId(), dados);
        EventoObra atualizacao = ServiceFixtures.eventoObra();
        atualizacao.setCategoria(CategoriaEvento.HOMENAGEM);

        EventoObra atualizado = service.atualizar(salvo.getId(), atualizacao);

        assertEquals(evento.getId(), atualizado.getEvento().getId());
        assertEquals(obra.getId(), atualizado.getObra().getId());
        assertEquals(CategoriaEvento.HOMENAGEM, atualizado.getCategoria());
        assertTrue(atualizado.getDestaque());
        assertEquals(1, service.listarPorEvento(evento.getId()).size());
        assertEquals(1, service.listarPorObra(obra.getId()).size());
        assertEquals(1, service.listarDestaquesDoEvento(evento.getId()).size());
    }

    @Test
    void deveAplicarDestaqueFalsoComoPadrao() {
        Evento evento = eventoRepository.saveAndFlush(ServiceFixtures.evento("Padrão"));
        ObraAudiovisual obra = salvarObra("Obra padrão");
        EventoObra dados = ServiceFixtures.eventoObra();
        dados.setDestaque(null);
        assertFalse(service.adicionarObra(evento.getId(), obra.getId(), dados).getDestaque());
    }

    @Test
    void deveRejeitarVinculoDuplicadoEDadosInvalidos() {
        Evento evento = eventoRepository.saveAndFlush(ServiceFixtures.evento("Duplicado"));
        ObraAudiovisual obra = salvarObra("Obra duplicada");
        service.adicionarObra(evento.getId(), obra.getId(), ServiceFixtures.eventoObra());
        assertEquals("Essa obra ja faz parte da programacao do evento",
                assertThrows(IllegalArgumentException.class,
                        () -> service.adicionarObra(evento.getId(), obra.getId(),
                                ServiceFixtures.eventoObra())).getMessage());
        assertThrows(IllegalArgumentException.class,
                () -> service.adicionarObra(evento.getId(), obra.getId() + 1, null));
        EventoObra semCategoria = ServiceFixtures.eventoObra();
        semCategoria.setCategoria(null);
        assertThrows(IllegalArgumentException.class,
                () -> service.adicionarObra(evento.getId(), obra.getId() + 1, semCategoria));
        EventoObra janela = ServiceFixtures.eventoObra();
        janela.setDataEstreiaEventoInicio(LocalDateTime.now().plusDays(2));
        janela.setDataEstreiaEventoFim(LocalDateTime.now().plusDays(1));
        assertThrows(IllegalArgumentException.class,
                () -> service.adicionarObra(evento.getId(), obra.getId() + 1, janela));
    }

    @Test
    void deveInformarAusenciaERemover() {
        assertThrows(NoSuchElementException.class, () -> service.buscarPorId(Long.MAX_VALUE));
        Evento evento = eventoRepository.saveAndFlush(ServiceFixtures.evento("Remover"));
        ObraAudiovisual obra = salvarObra("Remover vínculo");
        EventoObra salvo = service.adicionarObra(evento.getId(), obra.getId(), ServiceFixtures.eventoObra());
        service.remover(salvo.getId());
        repository.flush();
        assertFalse(repository.existsById(salvo.getId()));
    }
}
