package br.labprog.fluxarte.dto.response;

import java.util.List;

public record PrateleiraResponse(
    String id,
    String titulo,
    List<RecomendacaoResponse> itens
) {}
