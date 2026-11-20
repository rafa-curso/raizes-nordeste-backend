package br.com.raizesnordeste.gestao.dto.cardapio;

import br.com.raizesnordeste.gestao.domain.model.CategoriaCardapio;
import br.com.raizesnordeste.gestao.domain.model.ProdutoCardapio;
import java.math.BigDecimal;

public record ProdutoCardapioResponse(
    Long id,
    String codigoSku,
    String nome,
    String descricao,
    BigDecimal precoVenda,
    CategoriaCardapio categoria,
    boolean sazonalJunino,
    boolean disponivel
) {
    public static ProdutoCardapioResponse de(ProdutoCardapio produto) {
        return new ProdutoCardapioResponse(
            produto.getId(),
            produto.getCodigoSku(),
            produto.getNome(),
            produto.getDescricao(),
            produto.getPrecoVenda(),
            produto.getCategoria(),
            produto.isSazonalJunino(),
            produto.isDisponivel()
        );
    }
}
