package br.labprog.fluxarte.dto.auth;

import br.labprog.fluxarte.model.enums.TipoUsuario;

import java.util.UUID;

public record UsuarioResponse(UUID id, String nome, String email, String nomeExibicao, TipoUsuario tipoUsuario) {
}