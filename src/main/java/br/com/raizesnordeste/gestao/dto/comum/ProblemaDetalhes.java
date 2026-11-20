package br.com.raizesnordeste.gestao.dto.comum;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Padrão RFC 7807 (Problem Details for HTTP APIs) para tratamento e retorno de falhas.
 * Autor: Rafael Inácio da Silva (RU: 4736098)
 */
public record ProblemaDetalhes(
    String tipo,
    String titulo,
    int status,
    String detalhe,
    String instancia,
    List<CampoInvalido> camposComErro,
    LocalDateTime timestamp
) {
    public record CampoInvalido(String campo, String mensagemErro) {}

    public static ProblemaDetalhes construir(String tipo, String titulo, int status, String detalhe, String uri, List<CampoInvalido> erros) {
        return new ProblemaDetalhes(tipo, titulo, status, detalhe, uri, erros, LocalDateTime.now());
    }
}
