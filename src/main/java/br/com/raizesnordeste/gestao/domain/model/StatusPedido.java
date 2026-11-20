package br.com.raizesnordeste.gestao.domain.model;

/**
 * Estados do ciclo de vida operacional de um pedido.
 * Autor: Rafael Inácio da Silva (RU: 4736098)
 */
public enum StatusPedido {
    RECEBIDO_AGUARDANDO_PAGAMENTO,
    PAGAMENTO_CONFIRMADO,
    EM_PREPARO_COZINHA,
    PRONTO_PARA_ENTREGA,
    CONCLUIDO_ENTREGUE,
    CANCELADO_ESTORNADO
}
