package br.labprog.fluxarte.dto.response;

import br.labprog.fluxarte.model.enums.NomeGenero;
import java.util.Set;

public record GenerosPreferidosResponse(
    Set<NomeGenero> generos
) {}
