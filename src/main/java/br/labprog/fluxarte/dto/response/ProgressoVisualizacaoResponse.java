package br.labprog.fluxarte.dto.response;

public record ProgressoVisualizacaoResponse(
    Long id,
    Long obraId,
    String tituloObra,
    String posterUrlObra,
    Long midiaId,
    Integer posicaoSegundos,
    Integer duracaoTotalSegundos,
    Double percentualAssistido
) {}
