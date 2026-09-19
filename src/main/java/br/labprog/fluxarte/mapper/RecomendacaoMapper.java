package br.labprog.fluxarte.mapper;

import br.labprog.fluxarte.dto.response.RecomendacaoResponse;
import br.labprog.fluxarte.model.Recomendacao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecomendacaoMapper {

    @Mapping(target = "obraId", source = "obra.id")
    @Mapping(target = "tituloObra", source = "obra.titulo")
    @Mapping(target = "posterUrlObra", source = "obra.posterUrl")
    @Mapping(target = "classificacaoIndicativa", source = "obra.classificacaoIndicativa")
    RecomendacaoResponse toResponse(Recomendacao recomendacao);
}
