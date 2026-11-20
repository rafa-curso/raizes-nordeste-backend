package br.com.raizesnordeste.gestao.dto.pedido;

import br.com.raizesnordeste.gestao.domain.model.CanalAtendimento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CriarPedidoRequest(
    @NotNull(message = "O ID da unidade da franquia é obrigatório.")
    Long unidadeId,

    @NotNull(message = "O canal de atendimento de origem é obrigatório (ex: APLICATIVO_MOBILE, TOTEM_AUTOATENDIMENTO, BALCAO_HUMANO).")
    CanalAtendimento canalOrigem,

    String observacoes,

    Boolean resgatarDescontoFidelidade, // Se true, aplica desconto baseado no saldo de pontos

    @NotEmpty(message = "O pedido deve conter ao menos um item.")
    List<@Valid ItemPedidoRequest> itens
) {}
