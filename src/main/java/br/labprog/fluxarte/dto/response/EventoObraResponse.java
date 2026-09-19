package br.labprog.fluxarte.dto.response;

import br.labprog.fluxarte.model.enums.CategoriaEvento;

import java.time.LocalDateTime;

public record EventoObraResponse(
    Long id,
    Long obraId,
    String tituloObra,
    String posterUrlObra,
    CategoriaEvento categoria,
    LocalDateTime dataEstreiaEventoInicio,
    LocalDateTime dataEstreiaEventoFim,
    String premiacao,
    Boolean destaque
) {}
