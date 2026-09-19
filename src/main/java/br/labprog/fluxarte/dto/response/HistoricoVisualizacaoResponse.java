package br.labprog.fluxarte.dto.response;

import java.time.LocalDateTime;

public record HistoricoVisualizacaoResponse(
    Long id,
    Long obraId,
    String tituloObra,
    String posterUrlObra,
    Long midiaId,
    LocalDateTime dataVisualizacao,
    Integer tempoAssistidoSegundos,
    Boolean concluido
) {}
