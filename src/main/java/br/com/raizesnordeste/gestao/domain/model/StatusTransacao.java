package br.com.raizesnordeste.gestao.domain.model;

/**
 * Status retornado pelo Gateway Externo de Pagamento mockado.
 * Autor: Rafael Inácio da Silva (RU: 4736098)
 */
public enum StatusTransacao {
    PROCESSANDO,
    APROVADA,
    RECUSADA_SALDO_INSUFICIENTE,
    RECUSADA_SUSPEITA_FRAUDE,
    CANCELADA_REEMBOLSADA
}
