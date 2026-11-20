package br.com.raizesnordeste.gestao.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_rastreio", nullable = false, unique = true, length = 36)
    private String codigoRastreio;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "unidade_id", nullable = false)
    private UnidadeFranquia unidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "canal_origem", nullable = false, length = 35)
    private CanalAtendimento canalOrigem;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pedido", nullable = false, length = 35)
    private StatusPedido status;

    @Column(name = "valor_subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorSubtotal = BigDecimal.ZERO;

    @Column(name = "valor_desconto_fidelidade", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDescontoFidelidade = BigDecimal.ZERO;

    @Column(name = "valor_total_liquido", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotalLiquido = BigDecimal.ZERO;

    @Column(name = "pontos_gerados")
    private Integer pontosGerados = 0;

    @Column(name = "observacoes", length = 255)
    private String observacoes;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ItemPedido> itens = new ArrayList<>();

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TransacaoPagamento> transacoes = new ArrayList<>();

    @Column(name = "data_hora_abertura", nullable = false)
    private LocalDateTime dataHoraAbertura = LocalDateTime.now();

    @Column(name = "data_hora_atualizacao")
    private LocalDateTime dataHoraAtualizacao;

    public Pedido() {
        this.codigoRastreio = "RN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.status = StatusPedido.RECEBIDO_AGUARDANDO_PAGAMENTO;
        this.dataHoraAbertura = LocalDateTime.now();
    }

    public Pedido(Usuario cliente, UnidadeFranquia unidade, CanalAtendimento canalOrigem, String observacoes) {
        this();
        this.cliente = cliente;
        this.unidade = unidade;
        this.canalOrigem = canalOrigem;
        this.observacoes = observacoes;
    }

    public void adicionarItem(ProdutoCardapio produto, int quantidade) {
        ItemPedido item = new ItemPedido(this, produto, quantidade);
        this.itens.add(item);
        recalcularTotais();
    }

    public void adicionarTransacao(TransacaoPagamento transacao) {
        this.transacoes.add(transacao);
        transacao.setPedido(this);
    }

    public void recalcularTotais() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (ItemPedido item : this.itens) {
            if (item.getSubtotalItem() != null) {
                subtotal = subtotal.add(item.getSubtotalItem());
            }
        }
        this.valorSubtotal = subtotal;

        BigDecimal desconto = (this.valorDescontoFidelidade != null) ? this.valorDescontoFidelidade : BigDecimal.ZERO;
        this.valorTotalLiquido = subtotal.subtract(desconto).max(BigDecimal.ZERO);

        // Cada R$ 10 em compras gera 1 ponto no programa de fidelidade
        this.pontosGerados = this.valorTotalLiquido.divideToIntegralValue(BigDecimal.valueOf(10)).intValue();
        this.dataHoraAtualizacao = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigoRastreio() { return codigoRastreio; }
    public void setCodigoRastreio(String codigoRastreio) { this.codigoRastreio = codigoRastreio; }

    public Usuario getCliente() { return cliente; }
    public void setCliente(Usuario cliente) { this.cliente = cliente; }

    public UnidadeFranquia getUnidade() { return unidade; }
    public void setUnidade(UnidadeFranquia unidade) { this.unidade = unidade; }

    public CanalAtendimento getCanalOrigem() { return canalOrigem; }
    public void setCanalOrigem(CanalAtendimento canalOrigem) { this.canalOrigem = canalOrigem; }

    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) {
        this.status = status;
        this.dataHoraAtualizacao = LocalDateTime.now();
    }

    public BigDecimal getValorSubtotal() { return valorSubtotal; }
    public void setValorSubtotal(BigDecimal valorSubtotal) { this.valorSubtotal = valorSubtotal; }

    public BigDecimal getValorDescontoFidelidade() { return valorDescontoFidelidade; }
    public void setValorDescontoFidelidade(BigDecimal valorDescontoFidelidade) {
        this.valorDescontoFidelidade = valorDescontoFidelidade;
        recalcularTotais();
    }

    public BigDecimal getValorTotalLiquido() { return valorTotalLiquido; }
    public void setValorTotalLiquido(BigDecimal valorTotalLiquido) { this.valorTotalLiquido = valorTotalLiquido; }

    public Integer getPontosGerados() { return pontosGerados; }
    public void setPontosGerados(Integer pontosGerados) { this.pontosGerados = pontosGerados; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public List<ItemPedido> getItens() { return itens; }
    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
        recalcularTotais();
    }

    public List<TransacaoPagamento> getTransacoes() { return transacoes; }
    public void setTransacoes(List<TransacaoPagamento> transacoes) { this.transacoes = transacoes; }

    public LocalDateTime getDataHoraAbertura() { return dataHoraAbertura; }
    public void setDataHoraAbertura(LocalDateTime dataHoraAbertura) { this.dataHoraAbertura = dataHoraAbertura; }

    public LocalDateTime getDataHoraAtualizacao() { return dataHoraAtualizacao; }
    public void setDataHoraAtualizacao(LocalDateTime dataHoraAtualizacao) { this.dataHoraAtualizacao = dataHoraAtualizacao; }
}
