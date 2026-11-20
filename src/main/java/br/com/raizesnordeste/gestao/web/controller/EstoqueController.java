package br.com.raizesnordeste.gestao.web.controller;

import br.com.raizesnordeste.gestao.dto.estoque.InventarioEstoqueRequest;
import br.com.raizesnordeste.gestao.dto.estoque.InventarioEstoqueResponse;
import br.com.raizesnordeste.gestao.service.EstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/estoque")
@Tag(name = "4. Controle de Estoque", description = "Monitoramento e reposição descentralizada de estoque por unidade franqueada")
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @GetMapping("/unidade/{unidadeId}")
    @PreAuthorize("hasAnyRole('ADMIN_MATRIZ', 'GERENTE_LOJA')")
    @Operation(summary = "Lista o inventário completo de produtos para uma unidade franqueada")
    public ResponseEntity<List<InventarioEstoqueResponse>> listarPorUnidade(@PathVariable Long unidadeId) {
        return ResponseEntity.ok(estoqueService.listarPorUnidade(unidadeId));
    }

    @GetMapping("/alertas-minimo")
    @PreAuthorize("hasAnyRole('ADMIN_MATRIZ', 'GERENTE_LOJA')")
    @Operation(summary = "Lista itens de estoque que atingiram ou estão abaixo da margem de segurança")
    public ResponseEntity<List<InventarioEstoqueResponse>> listarAlertas() {
        return ResponseEntity.ok(estoqueService.listarAlertasEstoqueBaixo());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN_MATRIZ', 'GERENTE_LOJA')")
    @Operation(summary = "Cria ou ajusta manualmente o saldo de estoque de um produto em uma unidade")
    public ResponseEntity<InventarioEstoqueResponse> ajustarEstoque(
            @Valid @RequestBody InventarioEstoqueRequest request,
            Authentication authentication) {
        String responsavel = (authentication != null) ? authentication.getName() : "SISTEMA";
        InventarioEstoqueResponse response = estoqueService.salvarOuAtualizar(request, responsavel);
        return ResponseEntity.ok(response);
    }
}
