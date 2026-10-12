package br.labprog.fluxarte.dto.response;

import br.labprog.fluxarte.model.enums.TipoUsuario;

import java.time.LocalDate;
import java.util.UUID;

public record UsuarioResponse(
    UUID id,
    String nome,
    String email,
    String nomeExibicao,
    LocalDate dataNascimento,
    Boolean aceitaConteudoAdulto,
    TipoUsuario tipoUsuario,
    Boolean ativo
) {
    public UsuarioResponse(UUID id, String nomeExibicao, TipoUsuario tipoUsuario) {
        this(id, null, null, nomeExibicao, null, null, tipoUsuario, null);
    }
}
