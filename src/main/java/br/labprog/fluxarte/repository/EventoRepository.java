package br.labprog.fluxarte.repository;

import br.labprog.fluxarte.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByNomeContainingIgnoreCase(String nome);

    // eventos acontecendo na data informada (data null = sem restricao naquele limite)
    @Query("""
            SELECT e FROM Evento e
            WHERE (e.dataInicio IS NULL OR e.dataInicio <= :data)
              AND (e.dataFim IS NULL OR e.dataFim >= :data)
            """)
    List<Evento> findEmAndamento(@Param("data") LocalDate data);
}