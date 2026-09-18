package br.labprog.fluxarte.service;

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

    @Transactional
    public Evento cadastrar(Evento dados) {
        validar(dados);

        Evento evento = Evento.builder()
                .nome(dados.getNome().trim())
                .descricao(dados.getDescricao())
                .dataInicio(dados.getDataInicio())
                .dataFim(dados.getDataFim())
                .bannerUrl(dados.getBannerUrl())
                .build();

        return eventoRepository.save(evento);
    }

    @Transactional
    public Evento atualizar(Long id, Evento dados) {
        validar(dados);

        Evento evento = buscarPorId(id);

        evento.setNome(dados.getNome().trim());
        evento.setDescricao(dados.getDescricao());
        evento.setDataInicio(dados.getDataInicio());
        evento.setDataFim(dados.getDataFim());
        evento.setBannerUrl(dados.getBannerUrl());

        return eventoRepository.save(evento);
    }

    @Transactional(readOnly = true)
    public Evento buscarPorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Evento nao encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<Evento> listarTodos() {
        return eventoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Evento> buscarPorNome(String nome) {
        return eventoRepository.findByNomeContainingIgnoreCase(nome);
    }

    // RF19: eventos e festivais acontecendo hoje
    @Transactional(readOnly = true)
    public List<Evento> listarEmAndamento() {
        return eventoRepository.findEmAndamento(LocalDate.now());
    }

    // a programacao (EventoObra) do evento e removida junto, por causa do cascade
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