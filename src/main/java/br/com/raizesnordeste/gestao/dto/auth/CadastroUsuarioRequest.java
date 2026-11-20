package br.com.raizesnordeste.gestao.dto.auth;

import br.com.raizesnordeste.gestao.domain.model.PerfilAcesso;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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

    PerfilAcesso perfil, // Pode ser nulo no cadastro público, assumindo ROLE_CLIENTE

    @NotNull(message = "O consentimento dos termos de privacidade (LGPD) deve ser informado.")
    Boolean consentimentoLgpd
) {}
