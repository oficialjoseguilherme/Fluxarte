package br.labprog.fluxarte.dto.request;

import br.labprog.fluxarte.model.enums.CategoriaEvento;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record EventoObraRequest(
    @NotNull(message = "O ID da obra é obrigatório")
    Long obraId,

    @NotNull(message = "A categoria da obra no evento é obrigatória")
    CategoriaEvento categoria,

    LocalDateTime dataEstreiaEventoInicio,
    LocalDateTime dataEstreiaEventoFim,

    @Size(max = 80, message = "O texto da premiação não pode exceder 80 caracteres")
    String premiacao,

    Boolean destaque
) {
    public EventoObraRequest {
        if (destaque == null) destaque = false;
    }

    @AssertTrue(message = "As datas de início e término da estreia devem ser informadas juntas, e o término após o início")
    public boolean isPeriodoEstreiaValido() {
        if (dataEstreiaEventoInicio == null && dataEstreiaEventoFim == null) return true;
        if (dataEstreiaEventoInicio != null && dataEstreiaEventoFim != null) {
            return !dataEstreiaEventoFim.isBefore(dataEstreiaEventoInicio);
        }
        return false;
    }
}
