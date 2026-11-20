package br.com.raizesnordeste.gestao.dto.pedido;

import br.com.raizesnordeste.gestao.domain.model.StatusPedido;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AtualizarStatusPedidoRequest(
    @NotNull(message = "O novo status do pedido é obrigatório.")
    StatusPedido novoStatus,

    @Size(max = 255, message = "A justificativa deve ter no máximo 255 caracteres.")
    String justificativaOperacional
) {}
