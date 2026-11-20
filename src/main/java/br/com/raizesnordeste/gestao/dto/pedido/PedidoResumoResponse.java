package br.com.raizesnordeste.gestao.dto.pedido;

import br.com.raizesnordeste.gestao.domain.model.CanalAtendimento;
import br.com.raizesnordeste.gestao.domain.model.Pedido;
import br.com.raizesnordeste.gestao.domain.model.StatusPedido;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PedidoResumoResponse(
    Long id,
    String codigoRastreio,
    Long clienteId,
    String nomeCliente,
    Long unidadeId,
    String nomeUnidade,
    CanalAtendimento canalOrigem,
    StatusPedido status,
    BigDecimal valorSubtotal,
    BigDecimal valorDescontoFidelidade,
    BigDecimal valorTotalLiquido,
    Integer pontosGerados,
    int totalItens,
    LocalDateTime dataHoraAbertura
) {
    public static PedidoResumoResponse de(Pedido pedido) {
        return new PedidoResumoResponse(
            pedido.getId(),
            pedido.getCodigoRastreio(),
            pedido.getCliente().getId(),
            pedido.getCliente().getNomeCompleto(),
            pedido.getUnidade().getId(),
            pedido.getUnidade().getNomeFantasia(),
            pedido.getCanalOrigem(),
            pedido.getStatus(),
            pedido.getValorSubtotal(),
            pedido.getValorDescontoFidelidade(),
            pedido.getValorTotalLiquido(),
            pedido.getPontosGerados(),
            pedido.getItens().size(),
            pedido.getDataHoraAbertura()
        );
    }
}
