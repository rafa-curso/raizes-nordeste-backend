package br.com.raizesnordeste.gestao.dto.estoque;

import br.com.raizesnordeste.gestao.domain.model.InventarioEstoque;
import java.time.LocalDateTime;

public record InventarioEstoqueResponse(
    Long id,
    Long unidadeId,
    String nomeUnidade,
    Long produtoId,
    String nomeProduto,
    String codigoSku,
    Integer quantidadeDisponivel,
    Integer quantidadeMinimaSeguranca,
    boolean alertaEstoqueBaixo,
    LocalDateTime dataAtualizacao
) {
    public static InventarioEstoqueResponse de(InventarioEstoque estoque) {
        boolean baixo = estoque.getQuantidadeDisponivel() <= estoque.getQuantidadeMinimaSeguranca();
        return new InventarioEstoqueResponse(
            estoque.getId(),
            estoque.getUnidade().getId(),
            estoque.getUnidade().getNomeFantasia(),
            estoque.getProduto().getId(),
            estoque.getProduto().getNome(),
            estoque.getProduto().getCodigoSku(),
            estoque.getQuantidadeDisponivel(),
            estoque.getQuantidadeMinimaSeguranca(),
            baixo,
            estoque.getDataAtualizacao()
        );
    }
}
