package br.labprog.fluxarte.mapper;

import br.labprog.fluxarte.dto.request.UsuarioCadastroRequest;
import br.labprog.fluxarte.dto.response.UsuarioResponse;
import br.labprog.fluxarte.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "senhaHash", ignore = true)
    @Mapping(target = "tipoUsuario", ignore = true)
    @Mapping(target = "ativo", constant = "true")
    @Mapping(target = "generosPreferidos", ignore = true)
    @Mapping(target = "favoritos", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    Usuario toEntity(UsuarioCadastroRequest request);

    UsuarioResponse toResponse(Usuario usuario);
}
