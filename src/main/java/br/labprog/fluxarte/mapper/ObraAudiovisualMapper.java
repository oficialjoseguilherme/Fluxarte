package br.labprog.fluxarte.mapper;

import br.labprog.fluxarte.dto.request.ObraAudiovisualRequest;
import br.labprog.fluxarte.dto.response.ObraAudiovisualResponse;
import br.labprog.fluxarte.model.GeneroObra;
import br.labprog.fluxarte.model.ObraAudiovisual;
import br.labprog.fluxarte.model.enums.NomeGenero;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ObraAudiovisualMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    @Mapping(target = "generos", ignore = true)
    @Mapping(target = "midias", ignore = true)
    @Mapping(target = "eventoObras", ignore = true)
    @Mapping(target = "progressos", ignore = true)
    @Mapping(target = "historicos", ignore = true)
    @Mapping(target = "recomendacoes", ignore = true)
    ObraAudiovisual toEntity(ObraAudiovisualRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "disponivel", ignore = true)
    @Mapping(target = "generos", ignore = true)
    @Mapping(target = "midias", ignore = true)
    @Mapping(target = "eventoObras", ignore = true)
    @Mapping(target = "progressos", ignore = true)
    @Mapping(target = "historicos", ignore = true)
    @Mapping(target = "recomendacoes", ignore = true)
    void updateEntityFromRequest(ObraAudiovisualRequest request, @MappingTarget ObraAudiovisual obra);

    @Mapping(target = "generos", source = "generos", qualifiedByName = "mapGeneros")
    @Mapping(target = "exibivel", source = "obra", qualifiedByName = "mapExibivel")
    ObraAudiovisualResponse toResponse(ObraAudiovisual obra);

    @Named("mapGeneros")
    default Set<NomeGenero> mapGeneros(Set<GeneroObra> generos) {
        if (generos == null) return Set.of();
        return generos.stream()
                .filter(g -> g != null && g.getNome() != null)
                .map(g -> g.getNome())
                .collect(Collectors.toSet());
    }

    @Named("mapExibivel")
    default Boolean mapExibivel(ObraAudiovisual obra) {
        if (obra == null) return false;
        return obra.isExibivelEm(LocalDateTime.now());
    }
}
