package br.labprog.fluxarte.service;

import br.labprog.fluxarte.dto.request.EventoObraRequest;
import br.labprog.fluxarte.dto.response.EventoObraResponse;
import br.labprog.fluxarte.mapper.EventoObraMapper;
import br.labprog.fluxarte.model.Evento;
import br.labprog.fluxarte.model.EventoObra;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.repository.EventoObraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class EventoObraService {

    private final EventoObraRepository eventoObraRepository;
    private final EventoService eventoService;
    private final ObraAudiovisualService obraService;
    private final EventoObraMapper eventoObraMapper;

    @Transactional
    public EventoObraResponse adicionarObra(Long eventoId, EventoObraRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados da obra do evento sao obrigatorios");
        }
        if (eventoObraRepository.existsByEventoIdAndObraId(eventoId, request.obraId())) {
            throw new IllegalArgumentException("Essa obra ja faz parte da programacao do evento");
        }

        Evento evento = eventoService.buscarPorId(eventoId);
        ObraAudiovisual obra = obraService.buscarEntidadePorId(request.obraId());
        validarPeriodoEvento(evento, request.dataEstreiaEventoInicio(), request.dataEstreiaEventoFim());

        EventoObra eventoObra = eventoObraMapper.toEntity(request);
        eventoObra.setEvento(evento);
        eventoObra.setObra(obra);

        return eventoObraMapper.toResponse(eventoObraRepository.save(eventoObra));
    }

    @Transactional
    public EventoObra adicionarObra(Long eventoId, Long obraId, EventoObra dados) {
        validar(dados);

        if (eventoObraRepository.existsByEventoIdAndObraId(eventoId, obraId)) {
            throw new IllegalArgumentException("Essa obra ja faz parte da programacao do evento");
        }

        Evento evento = eventoService.buscarPorId(eventoId);
        ObraAudiovisual obra = obraService.buscarEntidadePorId(obraId);
        validarPeriodoEvento(evento, dados.getDataEstreiaEventoInicio(), dados.getDataEstreiaEventoFim());

        dados.setEvento(evento);
        dados.setObra(obra);
        if (dados.getDestaque() == null) {
            dados.setDestaque(false);
        }

        return eventoObraRepository.save(dados);
    }

    @Transactional
    public EventoObraResponse atualizar(Long id, EventoObraRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados da obra do evento sao obrigatorios");
        }

        EventoObra eventoObra = buscarEntidadePorId(id);
        validarPeriodoEvento(eventoObra.getEvento(), request.dataEstreiaEventoInicio(), request.dataEstreiaEventoFim());

        eventoObraMapper.updateEntityFromRequest(request, eventoObra);
        return eventoObraMapper.toResponse(eventoObraRepository.save(eventoObra));
    }

    @Transactional
    public EventoObra atualizar(Long id, EventoObra dados) {
        validar(dados);

        EventoObra eventoObra = buscarEntidadePorId(id);
        validarPeriodoEvento(eventoObra.getEvento(), dados.getDataEstreiaEventoInicio(), dados.getDataEstreiaEventoFim());

        eventoObra.setCategoria(dados.getCategoria());
        eventoObra.setDataEstreiaEventoInicio(dados.getDataEstreiaEventoInicio());
        eventoObra.setDataEstreiaEventoFim(dados.getDataEstreiaEventoFim());
        eventoObra.setPremiacao(dados.getPremiacao());
        eventoObra.setDestaque(dados.getDestaque() != null ? dados.getDestaque() : false);

        return eventoObraRepository.save(eventoObra);
    }

    @Transactional(readOnly = true)
    public EventoObraResponse buscarPorId(Long id) {
        return eventoObraMapper.toResponse(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public EventoObra buscarEntidadePorId(Long id) {
        return eventoObraRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Obra do evento nao encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<EventoObraResponse> listarPorEvento(Long eventoId) {
        return eventoObraRepository.findByEventoId(eventoId).stream()
                .map(eventoObraMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EventoObraResponse> listarPorObra(Long obraId) {
        return eventoObraRepository.findByObraId(obraId).stream()
                .map(eventoObraMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EventoObraResponse> listarDestaquesDoEvento(Long eventoId) {
        return eventoObraRepository.findByEventoIdAndDestaqueTrue(eventoId).stream()
                .map(eventoObraMapper::toResponse)
                .toList();
    }

    @Transactional
    public void remover(Long id) {
        EventoObra eventoObra = buscarEntidadePorId(id);
        eventoObraRepository.delete(eventoObra);
    }

    private void validarPeriodoEvento(Evento evento, LocalDateTime inicio, LocalDateTime fim) {
        if (evento.getDataInicio() != null && inicio != null && inicio.toLocalDate().isBefore(evento.getDataInicio())) {
            throw new IllegalArgumentException("Data de inicio da estreia nao pode ser anterior ao inicio do evento");
        }
        if (evento.getDataFim() != null && fim != null && fim.toLocalDate().isAfter(evento.getDataFim())) {
            throw new IllegalArgumentException("Data de fim da estreia nao pode ser posterior ao fim do evento");
        }
    }

    private void validar(EventoObra dados) {
        if (dados == null) {
            throw new IllegalArgumentException("Dados da obra do evento sao obrigatorios");
        }
        if (dados.getCategoria() == null) {
            throw new IllegalArgumentException("Categoria da obra no evento e obrigatoria");
        }
        if (dados.getDataEstreiaEventoInicio() != null && dados.getDataEstreiaEventoFim() != null
                && dados.getDataEstreiaEventoFim().isBefore(dados.getDataEstreiaEventoInicio())) {
            throw new IllegalArgumentException("Data fim da estreia nao pode ser anterior a data de inicio");
        }
    }
}