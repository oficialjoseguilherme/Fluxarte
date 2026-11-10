package br.labprog.fluxarte.dto.request;

import br.labprog.fluxarte.model.enums.NomeGenero;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record AtualizarGenerosPreferidosRequest(
    @NotNull(message = "A lista de gêneros preferidos não pode ser nula")
    Set<NomeGenero> generos
) {}
