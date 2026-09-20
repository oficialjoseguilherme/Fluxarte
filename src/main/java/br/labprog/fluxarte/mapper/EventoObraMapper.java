package br.labprog.fluxarte.mapper;

import br.labprog.fluxarte.dto.request.EventoObraRequest;
import br.labprog.fluxarte.dto.response.EventoObraResponse;
import br.labprog.fluxarte.model.EventoObra;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EventoObraMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "evento", ignore = true)
    @Mapping(target = "obra", ignore = true)
    EventoObra toEntity(EventoObraRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "evento", ignore = true)
    @Mapping(target = "obra", ignore = true)
    void updateEntityFromRequest(EventoObraRequest request, @MappingTarget EventoObra eventoObra);

    @Mapping(target = "obraId", source = "obra.id")
    @Mapping(target = "tituloObra", source = "obra.titulo")
    @Mapping(target = "posterUrlObra", source = "obra.posterUrl")
    EventoObraResponse toResponse(EventoObra eventoObra);
}
