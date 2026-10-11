package br.labprog.fluxarte.exceptions;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ErroValidacao extends ErroPadrao {

    private final List<CampoMensagem> erros = new ArrayList<>();

    public void adicionarErro(String nomeCampo, String mensagem) {
        erros.add(new CampoMensagem(nomeCampo, mensagem));
    }

    @Getter
    public static class CampoMensagem {
        private String nomeCampo;
        private String mensagem;

        public CampoMensagem(String nomeCampo, String mensagem) {
            this.nomeCampo = nomeCampo;
            this.mensagem = mensagem;
        }
    }
}