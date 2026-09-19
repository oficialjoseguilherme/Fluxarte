package br.labprog.fluxarte.dto.request;

import br.labprog.fluxarte.model.enums.TipoAtividade;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public record AtividadeUsuarioRequest(
    @NotNull(message = "O tipo da atividade é obrigatório")
    TipoAtividade tipoAtividade,

    String termoBusca,

    Long obraIdReferenciada,

    String metadadosJson
) {
    @AssertTrue(message = "Para atividades de busca, informe o termo pesquisado. Para as demais atividades, selecione uma obra.")
    public boolean isAtividadeValida() {
        if (tipoAtividade == TipoAtividade.BUSCA) {
            return termoBusca != null && !termoBusca.isBlank();
        } else {
            return obraIdReferenciada != null;
        }
    }
}
