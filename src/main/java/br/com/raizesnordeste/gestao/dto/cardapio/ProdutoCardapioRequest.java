package br.com.raizesnordeste.gestao.dto.cardapio;

import br.com.raizesnordeste.gestao.domain.model.CategoriaCardapio;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProdutoCardapioRequest(
    @NotBlank(message = "O código SKU é obrigatório.")
    @Size(max = 30, message = "O código SKU deve ter no máximo 30 caracteres.")
    String codigoSku,

    @NotBlank(message = "O nome do produto é obrigatório.")
    @Size(max = 100, message = "O nome do produto deve ter no máximo 100 caracteres.")
    String nome,

    @Size(max = 300, message = "A descrição deve ter no máximo 300 caracteres.")
    String descricao,

    @NotNull(message = "O preço de venda é obrigatório.")
    @DecimalMin(value = "0.01", message = "O preço de venda deve ser maior que zero.")
    BigDecimal precoVenda,

    @NotNull(message = "A categoria do cardápio é obrigatória.")
    CategoriaCardapio categoria,

    Boolean sazonalJunino,
    Boolean disponivel
) {}
