package br.labprog.fluxarte.repository;

import br.labprog.fluxarte.model.Recomendacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RecomendacaoRepository extends JpaRepository<Recomendacao, Long> {

    List<Recomendacao> findByUsuarioIdOrderByScoreDesc(UUID usuarioId);

    void deleteByUsuarioId(UUID usuarioId);
}