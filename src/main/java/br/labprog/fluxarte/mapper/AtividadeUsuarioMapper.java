package br.labprog.fluxarte.mapper;

import br.labprog.fluxarte.dto.request.AtividadeUsuarioRequest;
import br.labprog.fluxarte.dto.response.AtividadeUsuarioResponse;
import br.labprog.fluxarte.model.AtividadeUsuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AtividadeUsuarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "registradoEm", ignore = true)
    AtividadeUsuario toEntity(AtividadeUsuarioRequest request);

    AtividadeUsuarioResponse toResponse(AtividadeUsuario atividade);
}
