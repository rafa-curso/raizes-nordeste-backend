package br.com.raizesnordeste.gestao.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_produtos_cardapio")
public class ProdutoCardapio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_sku", nullable = false, unique = true, length = 30)
    private String codigoSku; // ex: CUSC-CARNE-SOL, TAP-QUEIJO-COALHO

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "descricao", length = 300)
    private String descricao;

    @Column(name = "preco_venda", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoVenda;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false, length = 30)
    private CategoriaCardapio categoria;

    @Column(name = "sazonal_junino", nullable = false)
    private boolean sazonalJunino = false;

    @Column(name = "disponivel", nullable = false)
    private boolean disponivel = true;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCadastro = LocalDateTime.now();

    public ProdutoCardapio() {}

    public ProdutoCardapio(String codigoSku, String nome, String descricao, BigDecimal precoVenda,
                           CategoriaCardapio categoria, boolean sazonalJunino) {
        this.codigoSku = codigoSku;
        this.nome = nome;
        this.descricao = descricao;
        this.precoVenda = precoVenda;
        this.categoria = categoria;
        this.sazonalJunino = sazonalJunino;
        this.disponivel = true;
        this.dataCadastro = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigoSku() { return codigoSku; }
    public void setCodigoSku(String codigoSku) { this.codigoSku = codigoSku; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(BigDecimal precoVenda) { this.precoVenda = precoVenda; }

    public CategoriaCardapio getCategoria() { return categoria; }
    public void setCategoria(CategoriaCardapio categoria) { this.categoria = categoria; }

    public boolean isSazonalJunino() { return sazonalJunino; }
    public void setSazonalJunino(boolean sazonalJunino) { this.sazonalJunino = sazonalJunino; }

    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
}
