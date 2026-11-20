package br.com.raizesnordeste.gestao.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_completo", nullable = false, length = 120)
    private String nomeCompleto;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senha;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(name = "perfil", nullable = false, length = 30)
    private PerfilAcesso perfil;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    // Campos de Governança e LGPD (Lei Geral de Proteção de Dados)
    @Column(name = "consentimento_lgpd", nullable = false)
    private boolean consentimentoLgpd = false;

    @Column(name = "data_aceite_lgpd")
    private LocalDateTime dataAceiteLgpd;

    // Programa de Fidelização
    @Column(name = "pontos_fidelidade", nullable = false)
    private Integer pontosFidelidade = 0;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCadastro = LocalDateTime.now();

    @Column(name = "data_ultimo_acesso")
    private LocalDateTime dataUltimoAcesso;

    public Usuario() {}

    public Usuario(String nomeCompleto, String email, String senha, String telefone, PerfilAcesso perfil, boolean consentimentoLgpd) {
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.perfil = perfil;
        this.consentimentoLgpd = consentimentoLgpd;
        if (consentimentoLgpd) {
            this.dataAceiteLgpd = LocalDateTime.now();
        }
        this.ativo = true;
        this.pontosFidelidade = 0;
        this.dataCadastro = LocalDateTime.now();
    }

    public void creditarPontos(int pontos) {
        if (pontos > 0) {
            this.pontosFidelidade += pontos;
        }
    }

    public void registrarConsentimentoLgpd(boolean aceito) {
        this.consentimentoLgpd = aceito;
        this.dataAceiteLgpd = aceito ? LocalDateTime.now() : null;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public PerfilAcesso getPerfil() { return perfil; }
    public void setPerfil(PerfilAcesso perfil) { this.perfil = perfil; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public boolean isConsentimentoLgpd() { return consentimentoLgpd; }
    public void setConsentimentoLgpd(boolean consentimentoLgpd) { this.consentimentoLgpd = consentimentoLgpd; }

    public LocalDateTime getDataAceiteLgpd() { return dataAceiteLgpd; }
    public void setDataAceiteLgpd(LocalDateTime dataAceiteLgpd) { this.dataAceiteLgpd = dataAceiteLgpd; }

    public Integer getPontosFidelidade() { return pontosFidelidade; }
    public void setPontosFidelidade(Integer pontosFidelidade) { this.pontosFidelidade = pontosFidelidade; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    public LocalDateTime getDataUltimoAcesso() { return dataUltimoAcesso; }
    public void setDataUltimoAcesso(LocalDateTime dataUltimoAcesso) { this.dataUltimoAcesso = dataUltimoAcesso; }
}
