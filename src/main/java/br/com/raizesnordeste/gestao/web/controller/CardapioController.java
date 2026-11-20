package br.com.raizesnordeste.gestao.web.controller;

import br.com.raizesnordeste.gestao.domain.model.CategoriaCardapio;
import br.com.raizesnordeste.gestao.dto.cardapio.ProdutoCardapioRequest;
import br.com.raizesnordeste.gestao.dto.cardapio.ProdutoCardapioResponse;
import br.com.raizesnordeste.gestao.service.CardapioService;
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
@RequestMapping("/api/v1/cardapio")
@Tag(name = "2. Cardápio Regional e Produtos", description = "Catálogo de pratos típicos nordestinos e gestão de itens sazonais")
public class CardapioController {

    private final CardapioService cardapioService;

    public CardapioController(CardapioService cardapioService) {
        this.cardapioService = cardapioService;
    }

    @GetMapping
    @Operation(summary = "Lista todos os produtos ativos do cardápio, com filtro opcional por categoria")
    public ResponseEntity<List<ProdutoCardapioResponse>> listarDisponiveis(
            @RequestParam(required = false) CategoriaCardapio categoria) {
        return ResponseEntity.ok(cardapioService.listarDisponiveis(categoria));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta detalhes de um produto por ID")
    public ResponseEntity<ProdutoCardapioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cardapioService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN_MATRIZ')")
    @Operation(summary = "Cadastra novo produto no cardápio (Apenas Administrador)")
    public ResponseEntity<ProdutoCardapioResponse> cadastrar(
            @Valid @RequestBody ProdutoCardapioRequest request,
            Authentication authentication) {
        String responsavel = (authentication != null) ? authentication.getName() : "ADMIN";
        ProdutoCardapioResponse response = cardapioService.cadastrar(request, responsavel);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
