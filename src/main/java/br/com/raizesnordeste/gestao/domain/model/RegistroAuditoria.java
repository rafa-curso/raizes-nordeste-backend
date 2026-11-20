package br.com.raizesnordeste.gestao.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_registros_auditoria")
public class RegistroAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false, length = 40)
    private TipoEventoAuditoria tipoEvento;

    @Column(name = "usuario_responsavel", nullable = false, length = 100)
    private String usuarioResponsavel;

    @Column(name = "canal_origem", length = 35)
    private String canalOrigem;

    @Column(name = "entidade_alvo", nullable = false, length = 50)
    private String entidadeAlvo;

    @Column(name = "identificador_alvo", length = 50)
    private String identificadorAlvo;

    @Column(name = "descricao_detalhada", length = 500)
    private String descricaoDetalhada;

    @Column(name = "data_hora_registro", nullable = false)
    private LocalDateTime dataHoraRegistro = LocalDateTime.now();

    public RegistroAuditoria() {}

    public RegistroAuditoria(TipoEventoAuditoria tipoEvento, String usuarioResponsavel, String canalOrigem,
                             String entidadeAlvo, String identificadorAlvo, String descricaoDetalhada) {
        this.tipoEvento = tipoEvento;
        this.usuarioResponsavel = (usuarioResponsavel != null) ? usuarioResponsavel : "SISTEMA_ANONIMO";
        this.canalOrigem = canalOrigem;
        this.entidadeAlvo = entidadeAlvo;
        this.identificadorAlvo = identificadorAlvo;
        this.descricaoDetalhada = descricaoDetalhada;
        this.dataHoraRegistro = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TipoEventoAuditoria getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(TipoEventoAuditoria tipoEvento) { this.tipoEvento = tipoEvento; }

    public String getUsuarioResponsavel() { return usuarioResponsavel; }
    public void setUsuarioResponsavel(String usuarioResponsavel) { this.usuarioResponsavel = usuarioResponsavel; }

    public String getCanalOrigem() { return canalOrigem; }
    public void setCanalOrigem(String canalOrigem) { this.canalOrigem = canalOrigem; }

    public String getEntidadeAlvo() { return entidadeAlvo; }
    public void setEntidadeAlvo(String entidadeAlvo) { this.entidadeAlvo = entidadeAlvo; }

    public String getIdentificadorAlvo() { return identificadorAlvo; }
    public void setIdentificadorAlvo(String identificadorAlvo) { this.identificadorAlvo = identificadorAlvo; }

    public String getDescricaoDetalhada() { return descricaoDetalhada; }
    public void setDescricaoDetalhada(String descricaoDetalhada) { this.descricaoDetalhada = descricaoDetalhada; }

    public LocalDateTime getDataHoraRegistro() { return dataHoraRegistro; }
    public void setDataHoraRegistro(LocalDateTime dataHoraRegistro) { this.dataHoraRegistro = dataHoraRegistro; }
}
