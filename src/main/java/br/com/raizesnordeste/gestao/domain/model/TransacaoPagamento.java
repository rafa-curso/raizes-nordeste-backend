package br.com.raizesnordeste.gestao.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_transacoes_pagamento")
public class TransacaoPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false, length = 30)
    private FormaPagamento formaPagamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_transacao", nullable = false, length = 35)
    private StatusTransacao statusTransacao;

    @Column(name = "valor_processado", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorProcessado;

    @Column(name = "nsu_autorizacao_gateway", length = 64)
    private String nsuAutorizacaoGateway;

    @Column(name = "mensagem_retorno_gateway", length = 255)
    private String mensagemRetornoGateway;

    @Column(name = "data_hora_processamento", nullable = false)
    private LocalDateTime dataHoraProcessamento = LocalDateTime.now();

    public TransacaoPagamento() {}

    public TransacaoPagamento(Pedido pedido, FormaPagamento formaPagamento, StatusTransacao statusTransacao,
                              BigDecimal valorProcessado, String nsuAutorizacaoGateway, String mensagemRetornoGateway) {
        this.pedido = pedido;
        this.formaPagamento = formaPagamento;
        this.statusTransacao = statusTransacao;
        this.valorProcessado = valorProcessado;
        this.nsuAutorizacaoGateway = nsuAutorizacaoGateway;
        this.mensagemRetornoGateway = mensagemRetornoGateway;
        this.dataHoraProcessamento = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public FormaPagamento getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(FormaPagamento formaPagamento) { this.formaPagamento = formaPagamento; }

    public StatusTransacao getStatusTransacao() { return statusTransacao; }
    public void setStatusTransacao(StatusTransacao statusTransacao) { this.statusTransacao = statusTransacao; }

    public BigDecimal getValorProcessado() { return valorProcessado; }
    public void setValorProcessado(BigDecimal valorProcessado) { this.valorProcessado = valorProcessado; }

    public String getNsuAutorizacaoGateway() { return nsuAutorizacaoGateway; }
    public void setNsuAutorizacaoGateway(String nsuAutorizacaoGateway) { this.nsuAutorizacaoGateway = nsuAutorizacaoGateway; }

    public String getMensagemRetornoGateway() { return mensagemRetornoGateway; }
    public void setMensagemRetornoGateway(String mensagemRetornoGateway) { this.mensagemRetornoGateway = mensagemRetornoGateway; }

    public LocalDateTime getDataHoraProcessamento() { return dataHoraProcessamento; }
    public void setDataHoraProcessamento(LocalDateTime dataHoraProcessamento) { this.dataHoraProcessamento = dataHoraProcessamento; }
}
