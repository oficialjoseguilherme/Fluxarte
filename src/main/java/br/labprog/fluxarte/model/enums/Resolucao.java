package br.labprog.fluxarte.model.enums;

public enum Resolucao {

    R480P("480p"),
    R720P("720p"),
    R1080P("1080p");

    private final String valor;

    Resolucao(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static Resolucao fromValor(String valor) {
        for (Resolucao r : values()) {
            if (r.valor.equals(valor)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Resolucao invalida: " + valor);
    }
}
