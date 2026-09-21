package br.labprog.fluxarte.service;

import br.labprog.fluxarte.dto.request.EventoRequest;
import br.labprog.fluxarte.dto.response.EventoResponse;
import br.labprog.fluxarte.mapper.EventoMapper;
import br.labprog.fluxarte.model.Evento;
import br.labprog.fluxarte.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;
    private final EventoMapper eventoMapper;

    @Transactional
    public EventoResponse cadastrar(EventoRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados do evento sao obrigatorios");
        }
        Evento evento = eventoMapper.toEntity(request);
        return eventoMapper.toResponse(eventoRepository.save(evento));
    }

    @Transactional
    public EventoResponse atualizar(Long id, EventoRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados do evento sao obrigatorios");
        }
        Evento evento = buscarPorId(id);
        eventoMapper.updateEntityFromRequest(request, evento);
        return eventoMapper.toResponse(eventoRepository.save(evento));
    }

    @Transactional
    public Evento cadastrar(Evento dados) {
        validar(dados);
        EventoRequest request = new EventoRequest(
                dados.getNome(),
                dados.getDescricao(),
                dados.getDataInicio(),
                dados.getDataFim(),
                dados.getBannerUrl()
        );
        Evento evento = eventoMapper.toEntity(request);
        return eventoRepository.save(evento);
    }

    @Transactional
    public Evento atualizar(Long id, Evento dados) {
        validar(dados);
        Evento evento = buscarPorId(id);
        EventoRequest request = new EventoRequest(
                dados.getNome(),
                dados.getDescricao(),
                dados.getDataInicio(),
                dados.getDataFim(),
                dados.getBannerUrl()
        );
        eventoMapper.updateEntityFromRequest(request, evento);
        return eventoRepository.save(evento);
    }

    @Transactional(readOnly = true)
    public Evento buscarPorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Evento nao encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public EventoResponse buscarPorIdResponse(Long id) {
        return eventoMapper.toResponse(buscarPorId(id));
    }

    @Transactional(readOnly = true)
    public List<Evento> listarTodos() {
        return eventoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<EventoResponse> listarTodosResponse() {
        return eventoRepository.findAll().stream()
                .map(eventoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Evento> buscarPorNome(String nome) {
        return eventoRepository.findByNomeContainingIgnoreCase(nome);
    }

    @Transactional(readOnly = true)
    public List<EventoResponse> buscarPorNomeResponse(String nome) {
        return eventoRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(eventoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Evento> listarEmAndamento() {
        return eventoRepository.findEmAndamento(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<EventoResponse> listarEmAndamentoResponse() {
        return eventoRepository.findEmAndamento(LocalDate.now()).stream()
                .map(eventoMapper::toResponse)
                .toList();
    }

    @Transactional
    public void excluir(Long id) {
        Evento evento = buscarPorId(id);
        eventoRepository.delete(evento);
    }

    private void validar(Evento dados) {
        if (dados == null) {
            throw new IllegalArgumentException("Dados do evento sao obrigatorios");
        }
        if (dados.getNome() == null || dados.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome do evento e obrigatorio");
        }
        if (dados.getDataInicio() != null && dados.getDataFim() != null
                && dados.getDataFim().isBefore(dados.getDataInicio())) {
            throw new IllegalArgumentException("Data fim do evento nao pode ser anterior a data de inicio");
        }
    }
}