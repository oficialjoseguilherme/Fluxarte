package br.labprog.fluxarte.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record UsuarioResponse(
    UUID id,
    String nome,
    String email,
    String nomeExibicao,
    LocalDate dataNascimento,
    Boolean aceitaConteudoAdulto,
    Boolean ativo
) {}
