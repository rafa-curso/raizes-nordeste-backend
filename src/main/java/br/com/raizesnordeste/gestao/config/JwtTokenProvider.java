package br.com.raizesnordeste.gestao.config;

import br.com.raizesnordeste.gestao.domain.model.Usuario;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Provedor JWT responsável pela geração, validação e extração de dados dos tokens.
 * A chave secreta é obtida exclusivamente via variável de ambiente APP_JWT_SECRET.
 *
 * Autor: Rafael Inácio da Silva (RU: 4736098)
 */
@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);
    private static final int TAMANHO_MINIMO_CHAVE = 32;

    private final SecretKey chaveSecreta;
    private final long tempoExpiracaoMs;
    private final String emissor;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String segredo,
            @Value("${app.jwt.expiration-ms}") long expiracao,
            @Value("${app.jwt.issuer:raizes-nordeste-api}") String emissor) {

        if (segredo == null || segredo.isBlank() || segredo.length() < TAMANHO_MINIMO_CHAVE) {
            throw new IllegalStateException(
                    "A variável APP_JWT_SECRET não está configurada ou possui menos de "
                    + TAMANHO_MINIMO_CHAVE + " caracteres. Configure-a como variável de ambiente.");
        }

        this.chaveSecreta = Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
        this.tempoExpiracaoMs = expiracao;
        this.emissor = emissor;
    }

    @PostConstruct
    public void validarConfiguracao() {
        log.info("JwtTokenProvider inicializado com sucesso. Emissor: {}, Expiração: {}ms", emissor, tempoExpiracaoMs);
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
