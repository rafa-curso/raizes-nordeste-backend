package br.com.raizesnordeste.gestao.dto.pedido;

import br.com.raizesnordeste.gestao.domain.model.ItemPedido;
import java.math.BigDecimal;

public record ItemPedidoResponse(
    Long id,
    Long produtoId,
    String nomeProduto,
    String codigoSku,
    BigDecimal precoUnitario,
    Integer quantidade,
    BigDecimal subtotalItem
) {
    public static ItemPedidoResponse de(ItemPedido item) {
        return new ItemPedidoResponse(
            item.getId(),
            item.getProduto().getId(),
            item.getProduto().getNome(),
            item.getProduto().getCodigoSku(),
            item.getPrecoUnitarioCobrado(),
            item.getQuantidade(),
            item.getSubtotalItem()
        );
    }
}
