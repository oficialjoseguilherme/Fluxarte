package br.labprog.fluxarte.mapper;

import br.labprog.fluxarte.dto.request.MidiaStreamingRequest;
import br.labprog.fluxarte.dto.response.MidiaStreamingResponse;
import br.labprog.fluxarte.model.MidiaStreaming;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MidiaStreamingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "obra", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    @Mapping(target = "progressos", ignore = true)
    @Mapping(target = "historicos", ignore = true)
    MidiaStreaming toEntity(MidiaStreamingRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "obra", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    @Mapping(target = "progressos", ignore = true)
    @Mapping(target = "historicos", ignore = true)
    void updateEntityFromRequest(MidiaStreamingRequest request, @MappingTarget MidiaStreaming midia);

    @Mapping(target = "obraId", source = "obra.id")
    MidiaStreamingResponse toResponse(MidiaStreaming midia);
}
