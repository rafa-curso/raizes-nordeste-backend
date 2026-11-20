package br.com.raizesnordeste.gestao.service;

import br.com.raizesnordeste.gestao.domain.model.FormaPagamento;
import br.com.raizesnordeste.gestao.domain.model.StatusTransacao;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Serviço de Integração Desacoplada com o Gateway de Pagamentos Externo.
 * Simula a comunicação assíncrona com processadoras financeiras externas (ex: Cielo, Stone, Banco Central / PIX),
 * em conformidade com os requisitos de desacoplamento do estudo de caso.
 * 
 * Autor: Rafael Inácio da Silva (RU: 4736098)
 */
@Service
public class GatewayPagamentoExternoService {

    public record RespostaGateway(
        StatusTransacao status,
        String nsuAutorizacao,
        String mensagem
    ) {}

    public RespostaGateway processarTransacao(FormaPagamento forma, BigDecimal valor, boolean simularAprovacao) {
        if (!simularAprovacao) {
            return new RespostaGateway(
                StatusTransacao.RECUSADA_SALDO_INSUFICIENTE,
                null,
                "Transação recusada pela instituição financeira: Saldo insuficiente ou limite não aprovado."
            );
        }

        String nsu = "NSU-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        String mensagem = String.format("Transação autorizada com sucesso via %s no valor de R$ %.2f", forma, valor);

        return new RespostaGateway(StatusTransacao.APROVADA, nsu, mensagem);
    }
}
