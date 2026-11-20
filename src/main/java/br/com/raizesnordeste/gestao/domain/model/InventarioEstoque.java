package br.com.raizesnordeste.gestao.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_inventario_estoque", uniqueConstraints = {
    @UniqueConstraint(name = "uk_unidade_produto", columnNames = {"unidade_id", "produto_id"})
})
public class InventarioEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "unidade_id", nullable = false)
    private UnidadeFranquia unidade;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "produto_id", nullable = false)
    private ProdutoCardapio produto;

    @Column(name = "quantidade_disponivel", nullable = false)
    private Integer quantidadeDisponivel;

    @Column(name = "quantidade_minima_seguranca", nullable = false)
    private Integer quantidadeMinimaSeguranca = 5;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao = LocalDateTime.now();

    public InventarioEstoque() {}

    public InventarioEstoque(UnidadeFranquia unidade, ProdutoCardapio produto, Integer quantidadeDisponivel, Integer quantidadeMinimaSeguranca) {
        this.unidade = unidade;
        this.produto = produto;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.quantidadeMinimaSeguranca = (quantidadeMinimaSeguranca != null) ? quantidadeMinimaSeguranca : 5;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public boolean temEstoqueDisponivel(int quantidadeDesejada) {
        return this.quantidadeDisponivel != null && this.quantidadeDisponivel >= quantidadeDesejada;
    }

    public void debitarEstoque(int quantidade) {
        if (!temEstoqueDisponivel(quantidade)) {
            throw new IllegalStateException("Saldo de estoque insuficiente para o produto: " + produto.getNome());
        }
        this.quantidadeDisponivel -= quantidade;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void restituirEstoque(int quantidade) {
        this.quantidadeDisponivel += quantidade;
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UnidadeFranquia getUnidade() { return unidade; }
    public void setUnidade(UnidadeFranquia unidade) { this.unidade = unidade; }

    public ProdutoCardapio getProduto() { return produto; }
    public void setProduto(ProdutoCardapio produto) { this.produto = produto; }

    public Integer getQuantidadeDisponivel() { return quantidadeDisponivel; }
    public void setQuantidadeDisponivel(Integer quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public Integer getQuantidadeMinimaSeguranca() { return quantidadeMinimaSeguranca; }
    public void setQuantidadeMinimaSeguranca(Integer quantidadeMinimaSeguranca) { this.quantidadeMinimaSeguranca = quantidadeMinimaSeguranca; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}
