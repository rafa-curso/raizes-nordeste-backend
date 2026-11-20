package br.com.raizesnordeste.gestao.dto.pagamento;

import br.com.raizesnordeste.gestao.domain.model.FormaPagamento;
import jakarta.validation.constraints.NotNull;

public record SolicitacaoPagamentoRequest(
    @NotNull(message = "A forma de pagamento é obrigatória (ex: PIX_ESTANTANEO, CARTAO_CREDITO).")
    FormaPagamento formaPagamento,

    Boolean simularAprovacao, // Permite simular aprovação (true) ou recusa (false) para testes de tolerância

    String numeroCartaoMock
) {
    public boolean deveAprovar() {
        return simularAprovacao == null || simularAprovacao;
    }
}
