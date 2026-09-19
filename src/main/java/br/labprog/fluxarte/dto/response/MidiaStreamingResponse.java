package br.labprog.fluxarte.dto.response;

import br.labprog.fluxarte.model.enums.FormatoMidia;
import br.labprog.fluxarte.model.enums.Resolucao;
import br.labprog.fluxarte.model.enums.TipoMidia;
import br.labprog.fluxarte.model.enums.TipoPlayer;

public record MidiaStreamingResponse(
    Long id,
    Long obraId,
    TipoMidia tipoMidia,
    TipoPlayer playerType,
    String eduplayEmbedUrl,
    String cdnStreamUrl,
    Resolucao resolucao,
    FormatoMidia formato,
    Integer duracaoSegundos,
    String thumbsSpriteUrl
) {}
