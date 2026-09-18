package br.labprog.fluxarte.repository;

import br.labprog.fluxarte.model.ProgressoVisualizacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProgressoVisualizacaoRepository extends JpaRepository<ProgressoVisualizacao, Long> {

    Optional<ProgressoVisualizacao> findByUsuarioIdAndMidiaId(UUID usuarioId, Long midiaId);

    List<ProgressoVisualizacao> findByUsuarioIdOrderByAtualizadoEmDesc(UUID usuarioId);
}