package br.com.raizesnordeste.gestao.dto.pedido;

import br.com.raizesnordeste.gestao.domain.model.CanalAtendimento;
import br.com.raizesnordeste.gestao.domain.model.Pedido;
import br.com.raizesnordeste.gestao.domain.model.StatusPedido;
import br.com.raizesnordeste.gestao.dto.pagamento.TransacaoPagamentoResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoDetalhadoResponse(
    Long id,
    String codigoRastreio,
    Long clienteId,
    String nomeCliente,
    String emailCliente,
    Long unidadeId,
    String nomeUnidade,
    CanalAtendimento canalOrigem,
    StatusPedido status,
    BigDecimal valorSubtotal,
    BigDecimal valorDescontoFidelidade,
    BigDecimal valorTotalLiquido,
    Integer pontosGerados,
    String observacoes,
    List<ItemPedidoResponse> itens,
    List<TransacaoPagamentoResponse> historicoTransacoes,
    LocalDateTime dataHoraAbertura,
    LocalDateTime dataHoraAtualizacao
) {
    public static PedidoDetalhadoResponse de(Pedido pedido) {
        List<ItemPedidoResponse> itensDto = pedido.getItens()
            .stream()
            .map(ItemPedidoResponse::de)
            .toList();

        List<TransacaoPagamentoResponse> transacoesDto = pedido.getTransacoes() != null
            ? pedido.getTransacoes().stream().map(TransacaoPagamentoResponse::de).toList()
            : List.of();

        return new PedidoDetalhadoResponse(
            pedido.getId(),
            pedido.getCodigoRastreio(),
            pedido.getCliente().getId(),
            pedido.getCliente().getNomeCompleto(),
            pedido.getCliente().getEmail(),
            pedido.getUnidade().getId(),
            pedido.getUnidade().getNomeFantasia(),
            pedido.getCanalOrigem(),
            pedido.getStatus(),
            pedido.getValorSubtotal(),
            pedido.getValorDescontoFidelidade(),
            pedido.getValorTotalLiquido(),
            pedido.getPontosGerados(),
            pedido.getObservacoes(),
            itensDto,
            transacoesDto,
            pedido.getDataHoraAbertura(),
            pedido.getDataHoraAtualizacao()
        );
    }
}
