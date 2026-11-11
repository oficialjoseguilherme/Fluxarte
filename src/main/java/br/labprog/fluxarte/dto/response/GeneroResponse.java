package br.labprog.fluxarte.dto.response;

import br.labprog.fluxarte.model.enums.NomeGenero;

public record GeneroResponse(
    NomeGenero id,
    String rotulo
) {}
