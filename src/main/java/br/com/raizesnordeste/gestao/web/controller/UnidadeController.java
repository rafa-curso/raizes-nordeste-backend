package br.com.raizesnordeste.gestao.web.controller;

import br.com.raizesnordeste.gestao.dto.unidade.UnidadeRequest;
import br.com.raizesnordeste.gestao.dto.unidade.UnidadeResponse;
import br.com.raizesnordeste.gestao.service.UnidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/unidades")
@Tag(name = "3. Unidades da Franquia", description = "Gestão de lojas físicas da rede com formatos Tradicional e Express")
public class UnidadeController {

    private final UnidadeService unidadeService;

    public UnidadeController(UnidadeService unidadeService) {
        this.unidadeService = unidadeService;
    }

    @GetMapping
    @Operation(summary = "Lista todas as unidades ativas da franquia Raízes do Nordeste")
    public ResponseEntity<List<UnidadeResponse>> listarAtivas() {
        return ResponseEntity.ok(unidadeService.listarAtivas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta informações de uma unidade por ID")
    public ResponseEntity<UnidadeResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(unidadeService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN_MATRIZ')")
    @Operation(summary = "Cadastra nova unidade física franqueada (Apenas Administrador)")
    public ResponseEntity<UnidadeResponse> cadastrar(
            @Valid @RequestBody UnidadeRequest request,
            Authentication authentication) {
        String responsavel = (authentication != null) ? authentication.getName() : "ADMIN";
        UnidadeResponse response = unidadeService.cadastrar(request, responsavel);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
