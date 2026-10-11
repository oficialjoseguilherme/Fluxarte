package br.labprog.fluxarte.exceptions;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class ErroPadrao {

    private Instant momento;
    private Integer status;
    private String erro;
    private String mensagem;
    private String caminho;

    public ErroPadrao(Instant momento, Integer status, String erro, String mensagem, String caminho) {
        this.momento = momento;
        this.status = status;
        this.erro = erro;
        this.mensagem = mensagem;
        this.caminho = caminho;
    }
}
