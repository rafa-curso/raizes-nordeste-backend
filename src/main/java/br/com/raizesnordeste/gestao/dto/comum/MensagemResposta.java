package br.com.raizesnordeste.gestao.dto.comum;

import java.time.LocalDateTime;

public record MensagemResposta(
    String mensagem,
    LocalDateTime timestamp
) {
    public static MensagemResposta de(String mensagem) {
        return new MensagemResposta(mensagem, LocalDateTime.now());
    }
}
