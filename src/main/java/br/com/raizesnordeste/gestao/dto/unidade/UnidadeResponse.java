package br.com.raizesnordeste.gestao.dto.unidade;

import br.com.raizesnordeste.gestao.domain.model.FormatoOperacao;
import br.com.raizesnordeste.gestao.domain.model.UnidadeFranquia;

public record UnidadeResponse(
    Long id,
    String codigoIdentificador,
    String nomeFantasia,
    String cidade,
    String uf,
    String enderecoCompleto,
    String telefoneContato,
    FormatoOperacao formatoOperacao,
    boolean ativo
) {
    public static UnidadeResponse de(UnidadeFranquia unidade) {
        return new UnidadeResponse(
            unidade.getId(),
            unidade.getCodigoIdentificador(),
            unidade.getNomeFantasia(),
            unidade.getCidade(),
            unidade.getUf(),
            unidade.getEnderecoCompleto(),
            unidade.getTelefoneContato(),
            unidade.getFormatoOperacao(),
            unidade.isAtivo()
        );
    }
}
