package br.com.raizesnordeste.gestao.dto.pedido;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemPedidoRequest(
    @NotNull(message = "O ID do produto é obrigatório.")
    Long produtoId,

    @NotNull(message = "A quantidade do item é obrigatória.")
    @Min(value = 1, message = "A quantidade mínima por item é 1.")
    Integer quantidade
) {}
