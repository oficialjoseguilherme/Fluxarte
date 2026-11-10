package br.labprog.fluxarte.dto.response;

import java.util.List;

public record RecomendacaoHomeResponse(
    RecomendacaoResponse destaque,
    List<PrateleiraResponse> prateleiras
) {}
