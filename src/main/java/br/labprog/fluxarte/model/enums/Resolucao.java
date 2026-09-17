package br.labprog.fluxarte.model.enums;

/**
 * Resolucao do arquivo de midia. Os valores do diagrama (480p, 720p, 1080p) nao sao
 * identificadores Java validos, entao as constantes carregam o valor real e a
 * persistencia usa ResolucaoConverter.
 */
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
