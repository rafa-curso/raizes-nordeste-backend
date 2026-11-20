package br.com.raizesnordeste.gestao.config;

import br.com.raizesnordeste.gestao.domain.model.Usuario;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey chaveSecreta;
    private final long tempoExpiracaoMs;
    private final String emissor;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String segredo,
            @Value("${app.jwt.expiration-ms}") long expiracao,
            @Value("${app.jwt.issuer:raizes-nordeste-api}") String emissor) {
        this.chaveSecreta = Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
        this.tempoExpiracaoMs = expiracao;
        this.emissor = emissor;
    }

    public String gerarToken(Usuario usuario) {
        Date agora = new Date();
        Date dataExpiracao = new Date(agora.getTime() + tempoExpiracaoMs);

        return Jwts.builder()
                .issuer(emissor)
                .subject(usuario.getEmail())
                .claim("usuarioId", usuario.getId())
                .claim("nomeCompleto", usuario.getNomeCompleto())
                .claim("perfil", usuario.getPerfil().name())
                .issuedAt(agora)
                .expiration(dataExpiracao)
                .signWith(chaveSecreta)
                .compact();
    }

    public String extrairEmail(String token) {
        return obterClaims(token).getSubject();
    }

    public String extrairPerfil(String token) {
        return (String) obterClaims(token).get("perfil");
    }

    public Long extrairUsuarioId(String token) {
        Number id = (Number) obterClaims(token).get("usuarioId");
        return id != null ? id.longValue() : null;
    }

    public boolean validarToken(String token) {
        try {
            obterClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public long getTempoExpiracaoMs() {
        return tempoExpiracaoMs;
    }

    private Claims obterClaims(String token) {
        return Jwts.parser()
                .verifyWith(chaveSecreta)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
