package br.labprog.fluxarte.dto.request;

import br.labprog.fluxarte.model.enums.FormatoMidia;
import br.labprog.fluxarte.model.enums.Resolucao;
import br.labprog.fluxarte.model.enums.TipoMidia;
import br.labprog.fluxarte.model.enums.TipoPlayer;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MidiaStreamingRequest(
        @NotNull(message = "O ID da obra audiovisual é obrigatório") Long obraId,

        @NotNull(message = "O tipo da mídia é obrigatório") TipoMidia tipoMidia,

        @NotNull(message = "O tipo de player é obrigatório") TipoPlayer playerType,

        String eduplayEmbedUrl,
        String cdnStreamUrl,

        Resolucao resolucao,
        FormatoMidia formato,

        @NotNull(message = "A duração em segundos é obrigatória") @Positive(message = "A duração deve ser maior que zero") Integer duracaoSegundos,

        String thumbsSpriteUrl) {
    @AssertTrue(message = "O player incorporado exige a URL do Eduplay, o player nativo exige a URL de transmissão da CDN.")
    public boolean isUrlPlayerValida() {
        if (playerType == TipoPlayer.EMBED) {
            return eduplayEmbedUrl != null && !eduplayEmbedUrl.isBlank();
        }
        if (playerType == TipoPlayer.NATIVE) {
            return cdnStreamUrl != null && !cdnStreamUrl.isBlank();
        }
        return true;
    }
}
