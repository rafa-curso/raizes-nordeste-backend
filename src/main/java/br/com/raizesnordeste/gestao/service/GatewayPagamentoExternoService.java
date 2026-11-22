package br.com.raizesnordeste.gestao.service;

import br.com.raizesnordeste.gestao.domain.model.FormaPagamento;
import br.com.raizesnordeste.gestao.domain.model.StatusTransacao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Serviço de Integração Desacoplada com o Gateway de Pagamentos Externo (SIMULADO).
 *
 * IMPORTANTE: Esta implementação é uma SIMULAÇÃO ACADÊMICA para fins de demonstração
 * da arquitetura desacoplada de pagamentos. NÃO se comunica com processadoras financeiras
 * reais (Cielo, Stone, PagSeguro, Banco Central / PIX ou similares).
 *
 * Cenários simulados:
 * - Aprovação: retorna status APROVADA com NSU (Número Sequencial Único) gerado localmente.
 * - Recusa: retorna status RECUSADA_SALDO_INSUFICIENTE com mensagem descritiva.
 *
 * Para uma integração real, esta classe deve ser substituída por um adapter que
 * se comunique com a API da processadora escolhida, mantendo a mesma interface.
 *
 * Autor: Rafael Inácio da Silva (RU: 4736098)
 */
@Service
public class GatewayPagamentoExternoService {

    private static final Logger log = LoggerFactory.getLogger(GatewayPagamentoExternoService.class);

    public record RespostaGateway(
        StatusTransacao status,
        String nsuAutorizacao,
        String mensagem
    ) {}

    /**
     * Processa uma transação financeira SIMULADA.
     *
     * @param forma Forma de pagamento selecionada pelo cliente.
     * @param valor Valor total da transação.
     * @param simularAprovacao Se true, simula aprovação; se false, simula recusa.
     * @return Resposta simulada do gateway com status, NSU e mensagem.
     */
    public RespostaGateway processarTransacao(FormaPagamento forma, BigDecimal valor, boolean simularAprovacao) {
        log.info("[GATEWAY SIMULADO] Processando transação: forma={}, valor=R$ {}, simularAprovacao={}",
                forma, valor, simularAprovacao);

        if (!simularAprovacao) {
            log.warn("[GATEWAY SIMULADO] Transação RECUSADA para o valor de R$ {}", valor);
            return new RespostaGateway(
                StatusTransacao.RECUSADA_SALDO_INSUFICIENTE,
                null,
                "Transação recusada pela instituição financeira simulada: Saldo insuficiente ou limite não aprovado."
            );
        }

        String nsu = "NSU-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        String mensagem = String.format("Transação autorizada com sucesso via %s no valor de R$ %.2f (simulação acadêmica)", forma, valor);

        log.info("[GATEWAY SIMULADO] Transação APROVADA — NSU: {}", nsu);
        return new RespostaGateway(StatusTransacao.APROVADA, nsu, mensagem);
    }
}
