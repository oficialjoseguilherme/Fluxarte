package br.labprog.fluxarte.dto.response;

import br.labprog.fluxarte.model.enums.TipoAtividade;

public record AtividadeUsuarioResponse(
    Long id,
    TipoAtividade tipoAtividade,
    String termoBusca,
    Long obraIdReferenciada,
    String metadadosJson
) {}
