package br.labprog.fluxarte.dto.response;

import br.labprog.fluxarte.model.enums.TipoUsuario;

import java.util.UUID;

public record UsuarioResponse(UUID id, String nomeExibicao, TipoUsuario tipoUsuario) {
}