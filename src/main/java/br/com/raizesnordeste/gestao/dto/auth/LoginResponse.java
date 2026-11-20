package br.com.raizesnordeste.gestao.dto.auth;

import br.com.raizesnordeste.gestao.domain.model.PerfilAcesso;

public record LoginResponse(
    String tokenAcesso,
    String tipoToken,
    Long usuarioId,
    String email,
    String nomeCompleto,
    PerfilAcesso perfil,
    long expiraEmMs
) {
    public static LoginResponse criar(String token, Long id, String email, String nome, PerfilAcesso perfil, long expiraEm) {
        return new LoginResponse(token, "Bearer", id, email, nome, perfil, expiraEm);
    }
}
