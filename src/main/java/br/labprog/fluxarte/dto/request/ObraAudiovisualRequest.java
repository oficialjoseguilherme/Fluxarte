package br.labprog.fluxarte.dto.request;

import br.labprog.fluxarte.model.enums.ClassificacaoIndicativa;
import br.labprog.fluxarte.model.enums.NomeGenero;
import br.labprog.fluxarte.model.enums.StatusObra;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Set;

public record ObraAudiovisualRequest(
        @NotBlank(message = "O título da obra é obrigatório") @Size(max = 225, message = "O título não pode exceder 225 caracteres") String titulo,

        @Size(max = 225, message = "O título original não pode exceder 225 caracteres") String tituloOriginal,

        @Size(max = 200, message = "O nome do diretor não pode exceder 200 caracteres") String diretor,

        String sinopse,

        @Min(value = 1800, message = "O ano de produção não pode ser anterior a 1800") Integer anoProducao,

        @Size(max = 10, message = "O código de idioma deve ter no máximo 10 caracteres") String idiomaOriginal,

        String posterUrl,
        String bannerUrl,

        Boolean disponivel,

        @NotNull(message = "O status da obra é obrigatório") StatusObra status,

        LocalDateTime dataInicioExibicao,
        LocalDateTime dataFimExibicao,

        @NotNull(message = "A classificação indicativa é obrigatória") ClassificacaoIndicativa classificacaoIndicativa,

        Set<NomeGenero> generos) {
    public ObraAudiovisualRequest {
        if (titulo != null)
            titulo = titulo.trim();
        if (diretor != null)
            diretor = diretor.trim();
        if (disponivel == null)
            disponivel = false;
        if (status == null)
            status = StatusObra.RASCUNHO;
    }

    @AssertTrue(message = "O ano de produção não pode ser no futuro")
    public boolean isAnoProducaoValido() {
        if (anoProducao == null)
            return true;
        return anoProducao <= Year.now().getValue();
    }

    @AssertTrue(message = "A data final de exibição não pode ser anterior à data de início")
    public boolean isJanelaExibicaoValida() {
        if (dataInicioExibicao != null && dataFimExibicao != null) {
            return !dataFimExibicao.isBefore(dataInicioExibicao);
        }
        return true;
    }
}
