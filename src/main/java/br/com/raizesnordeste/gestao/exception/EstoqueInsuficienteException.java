package br.com.raizesnordeste.gestao.exception;

public class EstoqueInsuficienteException extends RegraNegocioException {
    public EstoqueInsuficienteException(String nomeProduto, int disponivel, int solicitado) {
        super(String.format("Estoque insuficiente para '%s'. Saldo disponível: %d, solicitado: %d", nomeProduto, disponivel, solicitado));
    }
}
