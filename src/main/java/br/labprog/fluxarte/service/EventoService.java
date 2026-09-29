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
        Evento evento = buscarEntidadePorId(id);
        eventoMapper.updateEntityFromRequest(request, evento);
        return eventoMapper.toResponse(eventoRepository.save(evento));
    }

    @Transactional(readOnly = true)
    public EventoResponse buscarPorId(Long id) {
        return eventoMapper.toResponse(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public Evento buscarEntidadePorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Evento nao encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<EventoResponse> listarTodos() {
        return eventoRepository.findAll().stream()
                .map(eventoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EventoResponse> buscarPorNome(String nome) {
        return eventoRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(eventoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EventoResponse> listarEmAndamento() {
        return eventoRepository.findEmAndamento(LocalDate.now()).stream()
                .map(eventoMapper::toResponse)
                .toList();
    }

    @Transactional
    public void excluir(Long id) {
        Evento evento = buscarEntidadePorId(id);
        eventoRepository.delete(evento);
    }
}