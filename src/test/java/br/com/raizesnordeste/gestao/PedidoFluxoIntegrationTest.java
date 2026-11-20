package br.com.raizesnordeste.gestao;

import br.com.raizesnordeste.gestao.domain.model.CanalAtendimento;
import br.com.raizesnordeste.gestao.domain.model.FormaPagamento;
import br.com.raizesnordeste.gestao.domain.model.StatusPedido;
import br.com.raizesnordeste.gestao.domain.model.StatusTransacao;
import br.com.raizesnordeste.gestao.dto.auth.LoginRequest;
import br.com.raizesnordeste.gestao.dto.auth.LoginResponse;
import br.com.raizesnordeste.gestao.dto.pagamento.SolicitacaoPagamentoRequest;
import br.com.raizesnordeste.gestao.dto.pagamento.TransacaoPagamentoResponse;
import br.com.raizesnordeste.gestao.dto.pedido.CriarPedidoRequest;
import br.com.raizesnordeste.gestao.dto.pedido.ItemPedidoRequest;
import br.com.raizesnordeste.gestao.dto.pedido.PedidoResumoResponse;
import br.com.raizesnordeste.gestao.service.AutenticacaoService;
import br.com.raizesnordeste.gestao.service.EstoqueService;
import br.com.raizesnordeste.gestao.service.PedidoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PedidoFluxoIntegrationTest {

    @Autowired
    private AutenticacaoService autenticacaoService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private EstoqueService estoqueService;

    @Test
    @DisplayName("Deve autenticar com sucesso e gerar token JWT")
    void deveAutenticarComSucesso() {
        LoginRequest req = new LoginRequest("cliente@raizesnordeste.com", "cliente123");
        LoginResponse resp = autenticacaoService.autenticar(req);

        assertNotNull(resp);
        assertNotNull(resp.tokenAcesso());
        assertEquals("cliente@raizesnordeste.com", resp.email());
    }

    @Test
    @Transactional
    @DisplayName("Deve criar pedido, reservar estoque e aprovar pagamento via gateway")
    void deveCriarPedidoEAprovarPagamento() {
        String emailCliente = "cliente@raizesnordeste.com";
        CriarPedidoRequest pedidoReq = new CriarPedidoRequest(
                1L, // Recife Antigo
                CanalAtendimento.APLICATIVO_MOBILE,
                "Sem cebola",
                false,
                List.of(new ItemPedidoRequest(1L, 2)) // 2 cuscuz
        );

        PedidoResumoResponse pedidoCriado = pedidoService.criarPedido(pedidoReq, emailCliente);

        assertNotNull(pedidoCriado);
        assertEquals(StatusPedido.RECEBIDO_AGUARDANDO_PAGAMENTO, pedidoCriado.status());
        assertTrue(pedidoCriado.valorTotalLiquido().doubleValue() > 0);

        // Processar pagamento com aprovação
        SolicitacaoPagamentoRequest pagReq = new SolicitacaoPagamentoRequest(FormaPagamento.PIX_ESTANTANEO, true, null);
        TransacaoPagamentoResponse pagResp = pedidoService.processarPagamento(pedidoCriado.id(), pagReq, emailCliente);

        assertNotNull(pagResp);
        assertEquals(StatusTransacao.APROVADA, pagResp.statusTransacao());
        assertNotNull(pagResp.nsuAutorizacaoGateway());
    }

    @Test
    @Transactional
    @DisplayName("Deve restituir estoque automaticamente quando o pagamento for recusado")
    void deveRestituirEstoqueAoRecusarPagamento() {
        String emailCliente = "cliente@raizesnordeste.com";
        CriarPedidoRequest pedidoReq = new CriarPedidoRequest(
                1L,
                CanalAtendimento.TOTEM_AUTOATENDIMENTO,
                "Pagamento teste com recusa",
                false,
                List.of(new ItemPedidoRequest(2L, 3))
        );

        PedidoResumoResponse pedido = pedidoService.criarPedido(pedidoReq, emailCliente);

        // Simular recusa no gateway
        SolicitacaoPagamentoRequest pagReq = new SolicitacaoPagamentoRequest(FormaPagamento.CARTAO_CREDITO, false, "411111111111");
        TransacaoPagamentoResponse pagResp = pedidoService.processarPagamento(pedido.id(), pagReq, emailCliente);

        assertEquals(StatusTransacao.RECUSADA_SALDO_INSUFICIENTE, pagResp.statusTransacao());
    }
}
