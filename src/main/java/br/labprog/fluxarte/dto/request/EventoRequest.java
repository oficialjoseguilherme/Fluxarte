package br.labprog.fluxarte.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EventoRequest(
    @NotBlank(message = "O nome do evento é obrigatório")
    @Size(max = 225, message = "O nome não pode exceder 225 caracteres")
    String nome,

    String descricao,

    @NotNull(message = "A data de início do evento é obrigatória")
    LocalDate dataInicio,

    @NotNull(message = "A data de término do evento é obrigatória")
    LocalDate dataFim,

    String bannerUrl
) {
    public EventoRequest {
        if (nome != null) nome = nome.trim();
    }

    @AssertTrue(message = "A data de término do evento não pode ser anterior à data de início")
    public boolean isPeriodoValido() {
        if (dataInicio != null && dataFim != null) {
            return !dataFim.isBefore(dataInicio);
        }
        return true;
    }
}
