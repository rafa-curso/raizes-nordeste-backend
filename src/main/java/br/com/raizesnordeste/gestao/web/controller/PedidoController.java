package br.com.raizesnordeste.gestao.web.controller;

import br.com.raizesnordeste.gestao.domain.model.CanalAtendimento;
import br.com.raizesnordeste.gestao.domain.model.StatusPedido;
import br.com.raizesnordeste.gestao.dto.pagamento.SolicitacaoPagamentoRequest;
import br.com.raizesnordeste.gestao.dto.pagamento.TransacaoPagamentoResponse;
import br.com.raizesnordeste.gestao.dto.pedido.*;
import br.com.raizesnordeste.gestao.service.PedidoService;
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
@RequestMapping("/api/v1/pedidos")
@Tag(name = "5. Pedidos Multicanal e Pagamentos", description = "Ciclo de vida do pedido: criação, reserva de estoque, gateway desacoplado e avanço de status")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @Operation(summary = "Inicia novo pedido multicanal com reserva automática de estoque e cálculo de fidelidade")
    public ResponseEntity<PedidoResumoResponse> criarPedido(
            @Valid @RequestBody CriarPedidoRequest request,
            Authentication authentication) {
        PedidoResumoResponse response = pedidoService.criarPedido(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Lista pedidos. Clientes visualizam seus pedidos; Gerentes e Administradores visualizam todos com filtros")
    public ResponseEntity<List<PedidoResumoResponse>> listarPedidos(
            @RequestParam(required = false) CanalAtendimento canal,
            @RequestParam(required = false) StatusPedido status,
            @RequestParam(required = false) Long unidadeId,
            Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN_MATRIZ") || a.getAuthority().equals("ROLE_GERENTE_LOJA"));

        List<PedidoResumoResponse> lista = pedidoService.listarPedidos(canal, status, unidadeId, authentication.getName(), isAdmin);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta detalhes completos do pedido com itens e transações financeiras")
    public ResponseEntity<PedidoDetalhadoResponse> buscarPorId(
            @PathVariable Long id,
            Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN_MATRIZ") || a.getAuthority().equals("ROLE_GERENTE_LOJA"));

        return ResponseEntity.ok(pedidoService.buscarPorId(id, authentication.getName(), isAdmin));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN_MATRIZ', 'GERENTE_LOJA')")
    @Operation(summary = "Avança ou atualiza o status operacional do pedido (Cozinha / Entrega / Cancelamento)")
    public ResponseEntity<PedidoResumoResponse> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarStatusPedidoRequest request,
            Authentication authentication) {
        PedidoResumoResponse response = pedidoService.atualizarStatus(id, request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/pagamento")
    @Operation(summary = "Processa o pagamento via Gateway Externo Desacoplado. Se recusado, restitui o estoque automaticamente")
    public ResponseEntity<TransacaoPagamentoResponse> processarPagamento(
            @PathVariable Long id,
            @Valid @RequestBody SolicitacaoPagamentoRequest request,
            Authentication authentication) {
        TransacaoPagamentoResponse response = pedidoService.processarPagamento(id, request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/pagamentos")
    @Operation(summary = "Lista histórico de tentativas de transações financeiras do pedido")
    public ResponseEntity<List<TransacaoPagamentoResponse>> listarPagamentos(
            @PathVariable Long id,
            Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN_MATRIZ") || a.getAuthority().equals("ROLE_GERENTE_LOJA"));

        return ResponseEntity.ok(pedidoService.listarTransacoes(id, authentication.getName(), isAdmin));
    }
}
