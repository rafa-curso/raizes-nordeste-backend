package br.com.raizesnordeste.gestao.dto.estoque;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InventarioEstoqueRequest(
    @NotNull(message = "O ID da unidade é obrigatório.")
    Long unidadeId,

    @NotNull(message = "O ID do produto é obrigatório.")
    Long produtoId,

    @NotNull(message = "A quantidade disponível em estoque é obrigatória.")
    @Min(value = 0, message = "A quantidade não pode ser negativa.")
    Integer quantidadeDisponivel,

    @Min(value = 0, message = "A quantidade mínima de segurança não pode ser negativa.")
    Integer quantidadeMinimaSeguranca
) {}
