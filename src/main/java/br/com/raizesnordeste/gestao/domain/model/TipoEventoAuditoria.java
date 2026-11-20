package br.com.raizesnordeste.gestao.domain.model;

/**
 * Eventos monitorados pela trilha de auditoria e conformidade LGPD.
 * Autor: Rafael Inácio da Silva (RU: 4736098)
 */
public enum TipoEventoAuditoria {
    USUARIO_CADASTRADO,
    LOGIN_AUTORIZADO,
    LOGIN_FALHA,
    CONSENTIMENTO_LGPD_REGISTRADO,
    PEDIDO_INICIADO,
    ESTOQUE_RESERVADO,
    ESTOQUE_BAIXADO,
    ESTOQUE_RESTITUIDO,
    PAGAMENTO_SOLICITADO,
    PAGAMENTO_APROVADO,
    PAGAMENTO_RECUSADO,
    STATUS_PEDIDO_ALTERADO,
    AJUSTE_MANUAL_ESTOQUE,
    CARDAPIO_ATUALIZADO
}
