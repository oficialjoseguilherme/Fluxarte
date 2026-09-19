package br.labprog.fluxarte.dto.response;

import br.labprog.fluxarte.model.enums.ClassificacaoIndicativa;
import br.labprog.fluxarte.model.enums.NomeGenero;

import java.time.LocalDateTime;
import java.util.Set;

public record ObraAudiovisualResponse(
    Long id,
    String titulo,
    String tituloOriginal,
    String diretor,
    String sinopse,
    Integer anoProducao,
    String idiomaOriginal,
    String posterUrl,
    String bannerUrl,
    LocalDateTime dataInicioExibicao,
    LocalDateTime dataFimExibicao,
    ClassificacaoIndicativa classificacaoIndicativa,
    Set<NomeGenero> generos,
    Boolean exibivel
) {}
