package br.labprog.fluxarte.service;

import br.labprog.fluxarte.model.Evento;
import br.labprog.fluxarte.model.EventoObra;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.repository.EventoObraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class EventoObraService {

    private final EventoObraRepository eventoObraRepository;
    private final EventoService eventoService;
    private final ObraAudiovisualService obraService;

    // RF19: inclui uma obra na programacao de um evento
    @Transactional
    public EventoObra adicionarObra(Long eventoId, Long obraId, EventoObra dados) {
        validar(dados);

        if (eventoObraRepository.existsByEventoIdAndObraId(eventoId, obraId)) {
            throw new IllegalArgumentException("Essa obra ja faz parte da programacao do evento");
        }

        Evento evento = eventoService.buscarPorId(eventoId);
        ObraAudiovisual obra = obraService.buscarPorId(obraId);

        EventoObra eventoObra = EventoObra.builder()
                .evento(evento)
                .obra(obra)
                .categoria(dados.getCategoria())
                .dataEstreiaEventoInicio(dados.getDataEstreiaEventoInicio())
                .dataEstreiaEventoFim(dados.getDataEstreiaEventoFim())
                .premiacao(dados.getPremiacao())
                .destaque(dados.getDestaque() != null ? dados.getDestaque() : false)
                .build();

        return eventoObraRepository.save(eventoObra);
    }

    // evento e obra nao mudam: para trocar, remova e adicione novamente
    @Transactional
    public EventoObra atualizar(Long id, EventoObra dados) {
        validar(dados);

        EventoObra eventoObra = buscarPorId(id);

        eventoObra.setCategoria(dados.getCategoria());
        eventoObra.setDataEstreiaEventoInicio(dados.getDataEstreiaEventoInicio());
        eventoObra.setDataEstreiaEventoFim(dados.getDataEstreiaEventoFim());
        eventoObra.setPremiacao(dados.getPremiacao());
        eventoObra.setDestaque(dados.getDestaque() != null ? dados.getDestaque() : false);

        return eventoObraRepository.save(eventoObra);
    }

    @Transactional(readOnly = true)
    public EventoObra buscarPorId(Long id) {
        return eventoObraRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Obra do evento nao encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<EventoObra> listarPorEvento(Long eventoId) {
        return eventoObraRepository.findByEventoId(eventoId);
    }

    @Transactional(readOnly = true)
    public List<EventoObra> listarPorObra(Long obraId) {
        return eventoObraRepository.findByObraId(obraId);
    }

    @Transactional(readOnly = true)
    public List<EventoObra> listarDestaquesDoEvento(Long eventoId) {
        return eventoObraRepository.findByEventoIdAndDestaqueTrue(eventoId);
    }

    @Transactional
    public void remover(Long id) {
        EventoObra eventoObra = buscarPorId(id);
        eventoObraRepository.delete(eventoObra);
    }

    private void validar(EventoObra dados) {
        if (dados == null) {
            throw new IllegalArgumentException("Dados da obra do evento sao obrigatorios");
        }
        if (dados.getCategoria() == null) {
            throw new IllegalArgumentException("Categoria da obra no evento e obrigatoria");
        }
        // janela de estreia do evento precisa ser coerente
        if (dados.getDataEstreiaEventoInicio() != null && dados.getDataEstreiaEventoFim() != null
                && dados.getDataEstreiaEventoFim().isBefore(dados.getDataEstreiaEventoInicio())) {
            throw new IllegalArgumentException("Data fim da estreia nao pode ser anterior a data de inicio");
        }
    }
}