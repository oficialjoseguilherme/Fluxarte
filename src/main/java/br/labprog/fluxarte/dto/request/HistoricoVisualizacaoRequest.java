package br.labprog.fluxarte.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record HistoricoVisualizacaoRequest(
    @NotNull(message = "O ID da mídia é obrigatório")
    Long midiaId,

    @PositiveOrZero(message = "O tempo assistido não pode ser negativo")
    Integer tempoAssistidoSegundos,

    Boolean concluido
) {
    public HistoricoVisualizacaoRequest {
        if (concluido == null) concluido = false;
        if (tempoAssistidoSegundos == null) tempoAssistidoSegundos = 0;
    }
}
