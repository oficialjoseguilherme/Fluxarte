package br.labprog.fluxarte.model.enums;

public enum ClassificacaoIndicativa {

    LIVRE(0),
    DEZ(10),
    DOZE(12),
    QUATORZE(14),
    DEZESSEIS(16),
    DEZOITO(18);

    private final int idadeMinima;

    ClassificacaoIndicativa(int idadeMinima) {
        this.idadeMinima = idadeMinima;
    }

    public int getIdadeMinima() {
        return idadeMinima;
    }

    /** Conteudo adulto, sujeito ao filtro de preferencia familiar do usuario. */
    public boolean isAdulto() {
        return idadeMinima >= 18;
    }
}
