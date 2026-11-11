package br.labprog.fluxarte.model.enums;

import lombok.Getter;

@Getter
public enum NomeGenero {
    DOCUMENTARIO("Documentário"),
    FICCAO("Ficção"),
    ANIMACAO("Animação"),
    EXPERIMENTAL("Experimental"),
    VIDEOCLIPE("Videoclipe"),
    VIDEOARTE("Videoarte"),
    DRAMA("Drama"),
    COMEDIA("Comédia"),
    TERROR("Terror"),
    AVENTURA("Aventura"),
    ROMANCE("Romance"),
    INFANTIL("Infantil"),
    POLICIAL("Policial"),
    INSTITUCIONAL("Institucional"),
    HISTORICO("Histórico"),
    MUSICAL("Musical"),
    SUSPENSE("Suspense"),
    FANTASIA("Fantasia"),
    BIOGRAFIA("Biografia"),
    SCI_FI("Sci-Fi");

    private final String rotulo;

    NomeGenero(String rotulo) {
        this.rotulo = rotulo;
    }
}
