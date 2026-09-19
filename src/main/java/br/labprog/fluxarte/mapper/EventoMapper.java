package br.labprog.fluxarte.mapper;

import br.labprog.fluxarte.dto.request.EventoRequest;
import br.labprog.fluxarte.dto.response.EventoResponse;
import br.labprog.fluxarte.model.Evento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface EventoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventoObras", ignore = true)
    Evento toEntity(EventoRequest request);

    @Mapping(target = "emAndamento", source = "evento", qualifiedByName = "mapEmAndamento")
    EventoResponse toResponse(Evento evento);

    @Named("mapEmAndamento")
    default Boolean mapEmAndamento(Evento evento) {
        if (evento == null || evento.getDataInicio() == null || evento.getDataFim() == null) return false;
        LocalDate hoje = LocalDate.now();
        return !hoje.isBefore(evento.getDataInicio()) && !hoje.isAfter(evento.getDataFim());
    }
}
