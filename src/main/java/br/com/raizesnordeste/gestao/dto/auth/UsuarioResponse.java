package br.com.raizesnordeste.gestao.dto.auth;

import br.com.raizesnordeste.gestao.domain.model.PerfilAcesso;
import br.com.raizesnordeste.gestao.domain.model.Usuario;
import java.time.LocalDateTime;

public record UsuarioResponse(
    Long id,
    String nomeCompleto,
    String email,
    String telefone,
    PerfilAcesso perfil,
    boolean ativo,
    boolean consentimentoLgpd,
    LocalDateTime dataAceiteLgpd,
    Integer pontosFidelidade,
    LocalDateTime dataCadastro
) {
    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(
            usuario.getId(),
            usuario.getNomeCompleto(),
            usuario.getEmail(),
            usuario.getTelefone(),
            usuario.getPerfil(),
            usuario.isAtivo(),
            usuario.isConsentimentoLgpd(),
            usuario.getDataAceiteLgpd(),
            usuario.getPontosFidelidade(),
            usuario.getDataCadastro()
        );
    }
}
