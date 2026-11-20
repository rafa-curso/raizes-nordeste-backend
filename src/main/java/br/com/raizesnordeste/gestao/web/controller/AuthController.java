package br.com.raizesnordeste.gestao.web.controller;

import br.com.raizesnordeste.gestao.dto.auth.CadastroUsuarioRequest;
import br.com.raizesnordeste.gestao.dto.auth.LoginRequest;
import br.com.raizesnordeste.gestao.dto.auth.LoginResponse;
import br.com.raizesnordeste.gestao.dto.auth.UsuarioResponse;
import br.com.raizesnordeste.gestao.service.AutenticacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "1. Autenticação e Usuários", description = "Endpoints para login, registro de clientes e governança LGPD")
public class AuthController {

    private final AutenticacaoService autenticacaoService;

    public AuthController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @PostMapping("/login")
    @Operation(summary = "Realiza autenticação de usuário e gera token JWT")
    public ResponseEntity<LoginResponse> autenticar(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = autenticacaoService.autenticar(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cadastrar")
    @Operation(summary = "Cadastra novo cliente com consentimento explícito LGPD")
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody CadastroUsuarioRequest request) {
        UsuarioResponse response = autenticacaoService.cadastrarCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Retorna os dados do usuário atualmente autenticado")
    public ResponseEntity<UsuarioResponse> perfilAtual(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var usuario = autenticacaoService.buscarEntidadePorEmail(authentication.getName());
        return ResponseEntity.ok(UsuarioResponse.de(usuario));
    }
}
