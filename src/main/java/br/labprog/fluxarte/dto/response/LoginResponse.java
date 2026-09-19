package br.labprog.fluxarte.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record LoginResponse(
    String refreshToken,
    LocalDateTime expiraEm,
    String tipo,
    UUID usuarioId,
    String nome,
    String email
) {}
