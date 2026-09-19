package br.labprog.fluxarte.mapper;

import br.labprog.fluxarte.dto.request.ProgressoVisualizacaoRequest;
import br.labprog.fluxarte.dto.response.ProgressoVisualizacaoResponse;
import br.labprog.fluxarte.model.ProgressoVisualizacao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProgressoVisualizacaoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "obra", ignore = true)
    @Mapping(target = "midia", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    ProgressoVisualizacao toEntity(ProgressoVisualizacaoRequest request);

    @Mapping(target = "obraId", source = "obra.id")
    @Mapping(target = "tituloObra", source = "obra.titulo")
    @Mapping(target = "posterUrlObra", source = "obra.posterUrl")
    @Mapping(target = "midiaId", source = "midia.id")
    @Mapping(target = "duracaoTotalSegundos", source = "midia.duracaoSegundos")
    @Mapping(target = "percentualAssistido", expression = "java(progresso.getPercentualAssistido())")
    ProgressoVisualizacaoResponse toResponse(ProgressoVisualizacao progresso);
}
