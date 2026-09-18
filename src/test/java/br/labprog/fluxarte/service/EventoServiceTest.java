package br.labprog.fluxarte.service;

import br.labprog.fluxarte.model.Evento;
import br.labprog.fluxarte.repository.EventoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class EventoServiceTest extends AbstractServiceTest {

    @Autowired EventoService service;
    @Autowired EventoRepository repository;

    @Test
    void deveCadastrarAtualizarEConsultarEvento() {
        Evento salvo = service.cadastrar(ServiceFixtures.evento("  Festival  "));
        Evento dados = ServiceFixtures.evento("  Festival Atualizado  ");
        dados.setDescricao("Nova descrição");

        Evento atualizado = service.atualizar(salvo.getId(), dados);

        assertEquals("Festival Atualizado", atualizado.getNome());
        assertEquals("Nova descrição", atualizado.getDescricao());
        assertEquals(1, service.listarTodos().size());
        assertEquals(1, service.buscarPorNome("atualizado").size());
        assertEquals(1, service.listarEmAndamento().size());
        assertEquals(salvo.getId(), service.buscarPorId(salvo.getId()).getId());
    }

    @Test
    void deveValidarNomeEDatas() {
        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(null));
        Evento semNome = ServiceFixtures.evento(" ");
        assertEquals("Nome do evento e obrigatorio",
                assertThrows(IllegalArgumentException.class, () -> service.cadastrar(semNome)).getMessage());
        Evento datas = ServiceFixtures.evento("Datas");
        datas.setDataInicio(LocalDate.now().plusDays(2));
        datas.setDataFim(LocalDate.now().plusDays(1));
        assertEquals("Data fim do evento nao pode ser anterior a data de inicio",
                assertThrows(IllegalArgumentException.class, () -> service.cadastrar(datas)).getMessage());
    }

    @Test
    void deveInformarAusenciaEExcluir() {
        assertThrows(NoSuchElementException.class, () -> service.buscarPorId(Long.MAX_VALUE));
        Evento salvo = service.cadastrar(ServiceFixtures.evento("Excluir evento"));
        service.excluir(salvo.getId());
        repository.flush();
        assertFalse(repository.existsById(salvo.getId()));
    }
}
