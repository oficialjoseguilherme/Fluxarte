package br.labprog.fluxarte.dto.response;

import br.labprog.fluxarte.model.enums.ClassificacaoIndicativa;

public record RecomendacaoResponse(
    Long obraId,
    String tituloObra,
    String posterUrlObra,
    ClassificacaoIndicativa classificacaoIndicativa,
    Float score
) {}
