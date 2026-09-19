package br.labprog.fluxarte.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ProgressoVisualizacaoRequest(
    @NotNull(message = "O ID da mídia é obrigatório")
    Long midiaId,

    @NotNull(message = "A posição em segundos é obrigatória")
    @PositiveOrZero(message = "A posição em segundos não pode ser negativa")
    Integer posicaoSegundos
) {}
