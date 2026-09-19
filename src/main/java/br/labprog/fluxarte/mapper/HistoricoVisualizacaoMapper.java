package br.labprog.fluxarte.mapper;

import br.labprog.fluxarte.dto.request.HistoricoVisualizacaoRequest;
import br.labprog.fluxarte.dto.response.HistoricoVisualizacaoResponse;
import br.labprog.fluxarte.model.HistoricoVisualizacao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HistoricoVisualizacaoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "obra", ignore = true)
    @Mapping(target = "midia", ignore = true)
    @Mapping(target = "dataVisualizacao", ignore = true)
    HistoricoVisualizacao toEntity(HistoricoVisualizacaoRequest request);

    @Mapping(target = "obraId", source = "obra.id")
    @Mapping(target = "tituloObra", source = "obra.titulo")
    @Mapping(target = "posterUrlObra", source = "obra.posterUrl")
    @Mapping(target = "midiaId", source = "midia.id")
    HistoricoVisualizacaoResponse toResponse(HistoricoVisualizacao historico);
}
