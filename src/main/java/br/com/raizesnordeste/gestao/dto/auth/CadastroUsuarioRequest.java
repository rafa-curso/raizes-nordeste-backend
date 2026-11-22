package br.com.raizesnordeste.gestao.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de cadastro público de clientes.
 * O campo "perfil" foi removido por segurança: o perfil é definido
 * obrigatoriamente como ROLE_CLIENTE pelo serviço de autenticação,
 * impedindo que qualquer usuário se cadastre com privilégios administrativos.
 *
 * Autor: Rafael Inácio da Silva (RU: 4736098)
 */
public record CadastroUsuarioRequest(
    @NotBlank(message = "O nome completo é obrigatório.")
    @Size(min = 3, max = 120, message = "O nome deve ter entre 3 e 120 caracteres.")
    String nomeCompleto,

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    String email,

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, max = 50, message = "A senha deve ter entre 6 e 50 caracteres.")
    String senha,

    String telefone,

    @NotNull(message = "O consentimento dos termos de privacidade (LGPD) deve ser informado.")
    Boolean consentimentoLgpd
) {}
