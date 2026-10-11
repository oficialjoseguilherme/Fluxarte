package br.labprog.fluxarte.exceptions;

public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }

    public AcessoNegadoException() {
        super("Voce nao tem permissao para acessar este recurso");
    }
}
