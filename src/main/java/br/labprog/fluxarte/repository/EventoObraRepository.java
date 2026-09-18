package br.labprog.fluxarte.repository;

import br.labprog.fluxarte.model.EventoObra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoObraRepository extends JpaRepository<EventoObra, Long> {

    boolean existsByEventoIdAndObraId(Long eventoId, Long obraId);

    List<EventoObra> findByEventoId(Long eventoId);

    List<EventoObra> findByObraId(Long obraId);

    List<EventoObra> findByEventoIdAndDestaqueTrue(Long eventoId);
}