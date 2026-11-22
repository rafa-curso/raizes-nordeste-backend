package br.com.raizesnordeste.gestao.service;

import br.com.raizesnordeste.gestao.config.JwtTokenProvider;
import br.com.raizesnordeste.gestao.domain.model.PerfilAcesso;
import br.com.raizesnordeste.gestao.domain.model.TipoEventoAuditoria;
import br.com.raizesnordeste.gestao.domain.model.Usuario;
import br.com.raizesnordeste.gestao.domain.repository.UsuarioRepository;
import br.com.raizesnordeste.gestao.dto.auth.CadastroUsuarioRequest;
import br.com.raizesnordeste.gestao.dto.auth.LoginRequest;
import br.com.raizesnordeste.gestao.dto.auth.LoginResponse;
import br.com.raizesnordeste.gestao.dto.auth.UsuarioResponse;
import br.com.raizesnordeste.gestao.exception.ConflitoRegistroException;
import br.com.raizesnordeste.gestao.exception.RecursoNaoEncontradoException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuditoriaService auditoriaService;

    public AutenticacaoService(UsuarioRepository usuarioRepository,
                               PasswordEncoder passwordEncoder,
                               JwtTokenProvider jwtTokenProvider,
                               AuditoriaService auditoriaService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public LoginResponse autenticar(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new BadCredentialsException("E-mail ou senha inválidos."));

        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            auditoriaService.registrar(TipoEventoAuditoria.LOGIN_FALHA, request.email(), "API_AUTH",
                    "Usuario", null, "Tentativa de login com senha incorreta.");
            throw new BadCredentialsException("E-mail ou senha inválidos.");
        }

        if (!usuario.isAtivo()) {
            throw new BadCredentialsException("Conta de usuário inativa. Entre em contato com o suporte.");
        }

        usuario.setDataUltimoAcesso(LocalDateTime.now());
        usuarioRepository.save(usuario);

        String token = jwtTokenProvider.gerarToken(usuario);

        auditoriaService.registrar(TipoEventoAuditoria.LOGIN_AUTORIZADO, usuario.getEmail(), "API_AUTH",
                "Usuario", String.valueOf(usuario.getId()), "Login realizado com sucesso via JWT.");

        return LoginResponse.criar(token, usuario.getId(), usuario.getEmail(), usuario.getNomeCompleto(),
                usuario.getPerfil(), jwtTokenProvider.getTempoExpiracaoMs());
    }

    @Transactional
    public UsuarioResponse cadastrarCliente(CadastroUsuarioRequest request) {
        String emailFormatado = request.email().trim().toLowerCase();

        if (usuarioRepository.existsByEmail(emailFormatado)) {
            throw new ConflitoRegistroException("Já existe um cadastro ativo com o e-mail: " + emailFormatado);
        }

        // SEGURANÇA: O perfil é definido obrigatoriamente como ROLE_CLIENTE no cadastro público.
        // Perfis administrativos (ADMIN_MATRIZ, GERENTE_LOJA) devem ser criados exclusivamente
        // por procedimento controlado interno, nunca pela API pública.
        PerfilAcesso perfil = PerfilAcesso.ROLE_CLIENTE;

        Usuario novoUsuario = new Usuario(
                request.nomeCompleto().trim(),
                emailFormatado,
                passwordEncoder.encode(request.senha()),
                request.telefone(),
                perfil,
                Boolean.TRUE.equals(request.consentimentoLgpd())
        );

        Usuario salvo = usuarioRepository.save(novoUsuario);

        auditoriaService.registrar(TipoEventoAuditoria.USUARIO_CADASTRADO, salvo.getEmail(), "PORTAL_CADASTRO",
                "Usuario", String.valueOf(salvo.getId()), "Novo cadastro de " + salvo.getPerfil().name());

        if (salvo.isConsentimentoLgpd()) {
            auditoriaService.registrar(TipoEventoAuditoria.CONSENTIMENTO_LGPD_REGISTRADO, salvo.getEmail(), "LGPD_CHECK",
                    "Usuario", String.valueOf(salvo.getId()), "Consentimento explícito registrado para LGPD.");
        }

        return UsuarioResponse.de(salvo);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com o ID: " + id));
        return UsuarioResponse.de(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarEntidadePorEmail(String email) {
        return usuarioRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com o e-mail: " + email));
    }
}
