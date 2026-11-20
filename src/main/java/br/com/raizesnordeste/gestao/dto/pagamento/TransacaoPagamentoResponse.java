package br.com.raizesnordeste.gestao.dto.pagamento;

import br.com.raizesnordeste.gestao.domain.model.FormaPagamento;
import br.com.raizesnordeste.gestao.domain.model.StatusTransacao;
import br.com.raizesnordeste.gestao.domain.model.TransacaoPagamento;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransacaoPagamentoResponse(
    Long id,
    Long pedidoId,
    String codigoRastreioPedido,
    FormaPagamento formaPagamento,
    StatusTransacao statusTransacao,
    BigDecimal valorProcessado,
    String nsuAutorizacaoGateway,
    String mensagemRetornoGateway,
    LocalDateTime dataHoraProcessamento
) {
    public static TransacaoPagamentoResponse de(TransacaoPagamento transacao) {
        return new TransacaoPagamentoResponse(
            transacao.getId(),
            transacao.getPedido().getId(),
            transacao.getPedido().getCodigoRastreio(),
            transacao.getFormaPagamento(),
            transacao.getStatusTransacao(),
            transacao.getValorProcessado(),
            transacao.getNsuAutorizacaoGateway(),
            transacao.getMensagemRetornoGateway(),
            transacao.getDataHoraProcessamento()
        );
    }
}
