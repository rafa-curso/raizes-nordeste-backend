package br.com.raizesnordeste.gestao.exception;

public class ConflitoRegistroException extends RuntimeException {
    public ConflitoRegistroException(String mensagem) {
        super(mensagem);
    }
}
