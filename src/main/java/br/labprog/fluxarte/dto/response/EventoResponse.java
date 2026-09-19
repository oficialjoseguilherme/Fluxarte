package br.labprog.fluxarte.dto.response;

import java.time.LocalDate;

public record EventoResponse(
    Long id,
    String nome,
    String descricao,
    LocalDate dataInicio,
    LocalDate dataFim,
    String bannerUrl,
    Boolean emAndamento
) {}
