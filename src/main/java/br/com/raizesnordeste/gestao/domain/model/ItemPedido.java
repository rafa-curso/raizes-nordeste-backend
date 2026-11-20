package br.com.raizesnordeste.gestao.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_itens_pedido")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "produto_id", nullable = false)
    private ProdutoCardapio produto;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "preco_unitario_cobrado", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitarioCobrado;

    @Column(name = "subtotal_item", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotalItem;

    public ItemPedido() {}

    public ItemPedido(Pedido pedido, ProdutoCardapio produto, Integer quantidade) {
        this.pedido = pedido;
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitarioCobrado = produto.getPrecoVenda();
        this.subtotalItem = this.precoUnitarioCobrado.multiply(BigDecimal.valueOf(quantidade));
    }

    public void recalcularSubtotal() {
        if (this.precoUnitarioCobrado != null && this.quantidade != null) {
            this.subtotalItem = this.precoUnitarioCobrado.multiply(BigDecimal.valueOf(this.quantidade));
        }
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public ProdutoCardapio getProduto() { return produto; }
    public void setProduto(ProdutoCardapio produto) { this.produto = produto; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
        recalcularSubtotal();
    }

    public BigDecimal getPrecoUnitarioCobrado() { return precoUnitarioCobrado; }
    public void setPrecoUnitarioCobrado(BigDecimal precoUnitarioCobrado) {
        this.precoUnitarioCobrado = precoUnitarioCobrado;
        recalcularSubtotal();
    }

    public BigDecimal getSubtotalItem() { return subtotalItem; }
    public void setSubtotalItem(BigDecimal subtotalItem) { this.subtotalItem = subtotalItem; }
}
