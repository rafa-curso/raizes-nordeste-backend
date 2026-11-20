package br.com.raizesnordeste.gestao.exception;

public class AcessoNaoAutorizadoException extends RuntimeException {
    public AcessoNaoAutorizadoException(String mensagem) {
        super(mensagem);
    }
}
