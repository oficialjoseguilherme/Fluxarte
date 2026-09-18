package br.labprog.fluxarte.repository;

import br.labprog.fluxarte.model.HistoricoVisualizacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HistoricoVisualizacaoRepository extends JpaRepository<HistoricoVisualizacao, Long> {

    List<HistoricoVisualizacao> findByUsuarioIdOrderByDataVisualizacaoDesc(UUID usuarioId);
}