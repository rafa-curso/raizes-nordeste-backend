package br.com.raizesnordeste.gestao.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_unidades_franquia")
public class UnidadeFranquia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_identificador", nullable = false, unique = true, length = 20)
    private String codigoIdentificador; // ex: REC-01, CAR-01, FOR-01

    @Column(name = "nome_fantasia", nullable = false, length = 120)
    private String nomeFantasia;

    @Column(name = "cidade", nullable = false, length = 80)
    private String cidade;

    @Column(name = "uf", nullable = false, length = 2)
    private String uf;

    @Column(name = "endereco_completo", nullable = false, length = 255)
    private String enderecoCompleto;

    @Column(name = "telefone_contato", length = 20)
    private String telefoneContato;

    @Enumerated(EnumType.STRING)
    @Column(name = "formato_operacao", nullable = false, length = 30)
    private FormatoOperacao formatoOperacao; // TRADICIONAL_COMPLETA vs EXPRESS_REDUZIDA

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCadastro = LocalDateTime.now();

    public UnidadeFranquia() {}

    public UnidadeFranquia(String codigoIdentificador, String nomeFantasia, String cidade, String uf,
                           String enderecoCompleto, String telefoneContato, FormatoOperacao formatoOperacao) {
        this.codigoIdentificador = codigoIdentificador;
        this.nomeFantasia = nomeFantasia;
        this.cidade = cidade;
        this.uf = uf;
        this.enderecoCompleto = enderecoCompleto;
        this.telefoneContato = telefoneContato;
        this.formatoOperacao = formatoOperacao;
        this.ativo = true;
        this.dataCadastro = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigoIdentificador() { return codigoIdentificador; }
    public void setCodigoIdentificador(String codigoIdentificador) { this.codigoIdentificador = codigoIdentificador; }

    public String getNomeFantasia() { return nomeFantasia; }
    public void setNomeFantasia(String nomeFantasia) { this.nomeFantasia = nomeFantasia; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    public String getEnderecoCompleto() { return enderecoCompleto; }
    public void setEnderecoCompleto(String enderecoCompleto) { this.enderecoCompleto = enderecoCompleto; }

    public String getTelefoneContato() { return telefoneContato; }
    public void setTelefoneContato(String telefoneContato) { this.telefoneContato = telefoneContato; }

    public FormatoOperacao getFormatoOperacao() { return formatoOperacao; }
    public void setFormatoOperacao(FormatoOperacao formatoOperacao) { this.formatoOperacao = formatoOperacao; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
}
