package br.com.raizesnordeste.gestao.dto.unidade;

import br.com.raizesnordeste.gestao.domain.model.FormatoOperacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UnidadeRequest(
    @NotBlank(message = "O código identificador é obrigatório (ex: REC-01).")
    @Size(max = 20, message = "O código identificador deve ter no máximo 20 caracteres.")
    String codigoIdentificador,

    @NotBlank(message = "O nome fantasia da franquia é obrigatório.")
    @Size(max = 120, message = "O nome fantasia deve ter no máximo 120 caracteres.")
    String nomeFantasia,

    @NotBlank(message = "A cidade é obrigatória.")
    String cidade,

    @NotBlank(message = "A unidade federativa (UF) é obrigatória.")
    @Size(min = 2, max = 2, message = "UF deve possuir 2 caracteres (ex: PE, CE, RN).")
    String uf,

    @NotBlank(message = "O endereço completo é obrigatório.")
    String enderecoCompleto,

    String telefoneContato,

    @NotNull(message = "O formato de operação (TRADICIONAL_COMPLETA ou EXPRESS_REDUZIDA) é obrigatório.")
    FormatoOperacao formatoOperacao
) {}
