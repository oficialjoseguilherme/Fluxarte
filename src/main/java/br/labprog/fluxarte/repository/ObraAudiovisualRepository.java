package br.labprog.fluxarte.repository;

import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.enums.StatusObra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ObraAudiovisualRepository extends JpaRepository<ObraAudiovisual, Long> {

    List<ObraAudiovisual> findByTituloContainingIgnoreCase(String titulo);

    // RF09: busca direta por diretor
    List<ObraAudiovisual> findByDiretorContainingIgnoreCase(String diretor);

    List<ObraAudiovisual> findByStatus(StatusObra status);

    // RF13: obras disponiveis, ativas e dentro da janela de exibicao
    // (data null = sem restricao, mesma regra de ObraAudiovisual.isExibivelEm)
    @Query("""
            SELECT o FROM ObraAudiovisual o
            WHERE o.disponivel = true
              AND o.status = :status
              AND (o.dataInicioExibicao IS NULL OR o.dataInicioExibicao <= :agora)
              AND (o.dataFimExibicao IS NULL OR o.dataFimExibicao >= :agora)
            """)
    List<ObraAudiovisual> findExibiveis(@Param("agora") LocalDateTime agora,
                                        @Param("status") StatusObra status);
}