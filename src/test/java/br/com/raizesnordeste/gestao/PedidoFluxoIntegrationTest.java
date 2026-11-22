package br.com.raizesnordeste.gestao;

import br.com.raizesnordeste.gestao.domain.model.*;
import br.com.raizesnordeste.gestao.domain.repository.InventarioEstoqueRepository;
import br.com.raizesnordeste.gestao.domain.repository.ProdutoCardapioRepository;
import br.com.raizesnordeste.gestao.domain.repository.UsuarioRepository;
import br.com.raizesnordeste.gestao.config.JwtTokenProvider;
import br.com.raizesnordeste.gestao.dto.auth.CadastroUsuarioRequest;
import br.com.raizesnordeste.gestao.dto.auth.LoginRequest;
import br.com.raizesnordeste.gestao.dto.auth.LoginResponse;
import br.com.raizesnordeste.gestao.dto.auth.UsuarioResponse;
import br.com.raizesnordeste.gestao.dto.pagamento.SolicitacaoPagamentoRequest;
import br.com.raizesnordeste.gestao.dto.pagamento.TransacaoPagamentoResponse;
import br.com.raizesnordeste.gestao.dto.pedido.CriarPedidoRequest;
import br.com.raizesnordeste.gestao.dto.pedido.ItemPedidoRequest;
import br.com.raizesnordeste.gestao.dto.pedido.PedidoResumoResponse;
import br.com.raizesnordeste.gestao.exception.ConflitoRegistroException;
import br.com.raizesnordeste.gestao.exception.RegraNegocioException;
import br.com.raizesnordeste.gestao.service.AutenticacaoService;
import br.com.raizesnordeste.gestao.service.EstoqueService;
import br.com.raizesnordeste.gestao.service.PedidoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integração abrangentes para a API Raízes do Nordeste.
 * Cobrem autenticação, segurança no cadastro, fidelidade, estoque e pagamento.
 *
 * Autor: Rafael Inácio da Silva (RU: 4736098)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PedidoFluxoIntegrationTest {

    @Autowired
    private AutenticacaoService autenticacaoService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private EstoqueService estoqueService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private InventarioEstoqueRepository estoqueRepository;

    @Autowired
    private ProdutoCardapioRepository produtoRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MockMvc mockMvc;

    // =====================================================================
    // 1. TESTES DE AUTENTICAÇÃO E JWT
    // =====================================================================

    @Nested
    @DisplayName("1. Autenticação e JWT")
    class AutenticacaoTests {

        @Test
        @DisplayName("Deve autenticar cliente com sucesso e gerar token JWT")
        void deveAutenticarClienteComSucesso() {
            LoginRequest req = new LoginRequest("cliente@raizesnordeste.com", "cliente123");
            LoginResponse resp = autenticacaoService.autenticar(req);

            assertNotNull(resp, "Resposta de login não deve ser nula");
            assertNotNull(resp.tokenAcesso(), "Token JWT deve ser gerado");
            assertFalse(resp.tokenAcesso().isBlank(), "Token JWT não deve estar vazio");
            assertEquals("cliente@raizesnordeste.com", resp.email());
            assertTrue(jwtTokenProvider.validarToken(resp.tokenAcesso()));
            assertEquals(resp.email(), jwtTokenProvider.extrairEmail(resp.tokenAcesso()));
            assertEquals(PerfilAcesso.ROLE_CLIENTE.name(), jwtTokenProvider.extrairPerfil(resp.tokenAcesso()));
        }

        @Test
        @DisplayName("Deve autenticar administrador com sucesso e gerar token JWT")
        void deveAutenticarAdminComSucesso() {
            LoginRequest req = new LoginRequest("admin@raizesnordeste.com", "admin123");
            LoginResponse resp = autenticacaoService.autenticar(req);

            assertNotNull(resp);
            assertNotNull(resp.tokenAcesso());
            assertEquals("admin@raizesnordeste.com", resp.email());
        }

        @Test
        @DisplayName("Deve rejeitar login com senha incorreta")
        void deveRejeitarSenhaIncorreta() {
            LoginRequest req = new LoginRequest("cliente@raizesnordeste.com", "senhaerrada");
            assertThrows(BadCredentialsException.class, () -> autenticacaoService.autenticar(req));
        }

        @Test
        @DisplayName("Deve rejeitar login com e-mail inexistente")
        void deveRejeitarEmailInexistente() {
            LoginRequest req = new LoginRequest("naoexiste@raizesnordeste.com", "qualquer123");
            assertThrows(BadCredentialsException.class, () -> autenticacaoService.autenticar(req));
        }
    }

    // =====================================================================
    // 2. TESTES DE SEGURANÇA NO CADASTRO
    // =====================================================================

    @Nested
    @DisplayName("2. Segurança no Cadastro Público")
    class CadastroSegurancaTests {

        @Test
        @DisplayName("Cadastro público deve criar usuário obrigatoriamente como ROLE_CLIENTE")
        void cadastroPublicoDeveSerSempreCliente() {
            CadastroUsuarioRequest req = new CadastroUsuarioRequest(
                    "Novo Usuário Teste",
                    "novousuario@teste.com",
                    "senha123",
                    "(81) 99999-0000",
                    true
            );

            UsuarioResponse resp = autenticacaoService.cadastrarCliente(req);

            assertNotNull(resp);
            assertEquals(PerfilAcesso.ROLE_CLIENTE, resp.perfil(),
                    "O perfil deve ser obrigatoriamente ROLE_CLIENTE no cadastro público");
            assertTrue(resp.consentimentoLgpd());
        }

        @Test
        @DisplayName("API pública deve ignorar tentativa de cadastro como administrador")
        void apiPublicaNaoDeveAceitarPerfilAdministrativo() throws Exception {
            String payload = """
                    {
                      "nomeCompleto": "Tentativa Administrador",
                      "email": "tentativa.admin@teste.com",
                      "senha": "senha123",
                      "telefone": "(81) 99999-0001",
                      "perfil": "ROLE_ADMIN_MATRIZ",
                      "consentimentoLgpd": true
                    }
                    """;

            mockMvc.perform(post("/api/v1/auth/cadastrar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payload))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.perfil").value("ROLE_CLIENTE"));

            Usuario salvo = usuarioRepository.findByEmail("tentativa.admin@teste.com").orElseThrow();
            assertEquals(PerfilAcesso.ROLE_CLIENTE, salvo.getPerfil());
        }

        @Test
        @DisplayName("Cliente autenticado não deve acessar a trilha de auditoria")
        void clienteNaoDeveAcessarAuditoria() throws Exception {
            String tokenCliente = autenticacaoService.autenticar(
                    new LoginRequest("cliente@raizesnordeste.com", "cliente123")).tokenAcesso();

            mockMvc.perform(get("/api/v1/auditoria")
                            .header("Authorization", "Bearer " + tokenCliente))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Administrador autenticado deve acessar a trilha de auditoria")
        void administradorDeveAcessarAuditoria() throws Exception {
            String tokenAdmin = autenticacaoService.autenticar(
                    new LoginRequest("admin@raizesnordeste.com", "admin123")).tokenAcesso();

            mockMvc.perform(get("/api/v1/auditoria")
                            .header("Authorization", "Bearer " + tokenAdmin))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Não deve permitir cadastro com e-mail duplicado")
        void naoDevePermitirEmailDuplicado() {
            CadastroUsuarioRequest req = new CadastroUsuarioRequest(
                    "Duplicado Teste",
                    "cliente@raizesnordeste.com",
                    "senha123",
                    null,
                    true
            );

            assertThrows(ConflitoRegistroException.class,
                    () -> autenticacaoService.cadastrarCliente(req),
                    "Deve lançar exceção para e-mail duplicado");
        }

        @Test
        @DisplayName("Cadastro sem consentimento LGPD deve registrar consentimento como false")
        void cadastroSemConsentimentoLgpd() {
            CadastroUsuarioRequest req = new CadastroUsuarioRequest(
                    "Sem LGPD Teste",
                    "semlgpd@teste.com",
                    "senha123",
                    null,
                    false
            );

            UsuarioResponse resp = autenticacaoService.cadastrarCliente(req);
            assertNotNull(resp);
            assertFalse(resp.consentimentoLgpd());
        }
    }

    // =====================================================================
    // 3. TESTES DO PROGRAMA DE FIDELIDADE
    // =====================================================================

    @Nested
    @DisplayName("3. Programa de Fidelidade — Consumo correto de pontos")
    class FidelidadeTests {

        @Test
        @Transactional
        @DisplayName("Deve calcular desconto e consumir somente os pontos efetivamente utilizados")
        void deveConsumirPontosCorretamente() {
            // Cliente tem 50 pontos (seed data). Compra de R$ 22,90 (1 cuscuz CUSC-001).
            // Subtotal: R$ 22,90
            // 50% do subtotal: R$ 11,45
            // 50 pontos = R$ 25,00 de desconto bruto
            // Desconto efetivo: R$ 11,45 (limitado a 50%)
            // Pontos para gerar R$ 11,45 de desconto: floor(11,45/5) * 10 = 20 pontos
            // Saldo restante: 50 - 20 = 30 pontos
            String emailCliente = "cliente@raizesnordeste.com";

            CriarPedidoRequest pedidoReq = new CriarPedidoRequest(
                    1L,
                    CanalAtendimento.APLICATIVO_MOBILE,
                    "Teste fidelidade",
                    true, // Resgatar desconto
                    List.of(new ItemPedidoRequest(1L, 1))
            );

            PedidoResumoResponse pedido = pedidoService.criarPedido(pedidoReq, emailCliente);

            assertNotNull(pedido);
            assertEquals(0, new BigDecimal("10.00").compareTo(pedido.valorDescontoFidelidade()),
                    "O desconto deve respeitar blocos de R$ 5 sem ultrapassar 50% do subtotal");

            // Verificar que o saldo de pontos foi decrementado corretamente
            Usuario cliente = usuarioRepository.findByEmail(emailCliente).orElseThrow();
            assertEquals(30, cliente.getPontosFidelidade(),
                    "Somente 20 pontos devem ser consumidos");
        }

        @Test
        @Transactional
        @DisplayName("Exemplo obrigatório: 50 pontos, compra R$ 20 → desconto R$ 10, consome 20 pontos, saldo 30")
        void exemploObrigatorioFidelidade() {
            String emailCliente = "cliente@raizesnordeste.com";
            ProdutoCardapio produto = produtoRepository.findById(1L).orElseThrow();
            produto.setPrecoVenda(new BigDecimal("20.00"));
            produtoRepository.saveAndFlush(produto);

            CriarPedidoRequest pedidoReq = new CriarPedidoRequest(
                    1L,
                    CanalAtendimento.APLICATIVO_MOBILE,
                    "Exemplo fidelidade obrigatório",
                    true,
                    List.of(new ItemPedidoRequest(1L, 1))
            );

            PedidoResumoResponse pedido = pedidoService.criarPedido(pedidoReq, emailCliente);

            // Verificar desconto e saldo de pontos
            Usuario cliente = usuarioRepository.findByEmail(emailCliente).orElseThrow();

            assertEquals(0, new BigDecimal("20.00").compareTo(pedido.valorSubtotal()));
            assertEquals(0, new BigDecimal("10.00").compareTo(pedido.valorDescontoFidelidade()));
            assertEquals(0, new BigDecimal("10.00").compareTo(pedido.valorTotalLiquido()));
            assertEquals(30, cliente.getPontosFidelidade());
        }

        @Test
        @Transactional
        @DisplayName("Saldo inferior a 10 pontos não deve gerar desconto nem consumo")
        void saldoInsuficienteParaBlocoNaoDeveSerConsumido() {
            String emailCliente = "cliente@raizesnordeste.com";
            Usuario cliente = usuarioRepository.findByEmail(emailCliente).orElseThrow();
            cliente.setPontosFidelidade(9);
            usuarioRepository.saveAndFlush(cliente);

            PedidoResumoResponse pedido = pedidoService.criarPedido(new CriarPedidoRequest(
                    1L, CanalAtendimento.PORTAL_WEB, null, true,
                    List.of(new ItemPedidoRequest(1L, 1))), emailCliente);

            assertEquals(0, BigDecimal.ZERO.compareTo(pedido.valorDescontoFidelidade()));
            assertEquals(9, usuarioRepository.findByEmail(emailCliente).orElseThrow().getPontosFidelidade());
        }

        @Test
        @Transactional
        @DisplayName("Sem resgate de fidelidade, pontos devem permanecer intactos")
        void semResgatePontosDevemPermanecer() {
            String emailCliente = "cliente@raizesnordeste.com";
            int pontosAntes = usuarioRepository.findByEmail(emailCliente).orElseThrow().getPontosFidelidade();

            CriarPedidoRequest pedidoReq = new CriarPedidoRequest(
                    1L,
                    CanalAtendimento.APLICATIVO_MOBILE,
                    "Sem resgate",
                    false, // NÃO resgatar
                    List.of(new ItemPedidoRequest(1L, 1))
            );

            pedidoService.criarPedido(pedidoReq, emailCliente);

            int pontosDepois = usuarioRepository.findByEmail(emailCliente).orElseThrow().getPontosFidelidade();
            assertEquals(pontosAntes, pontosDepois,
                    "Pontos devem permanecer inalterados quando não há resgate");
        }
    }

    // =====================================================================
    // 4. TESTES DE PEDIDO E PAGAMENTO
    // =====================================================================

    @Nested
    @DisplayName("4. Pedidos e Pagamento")
    class PedidoPagamentoTests {

        @Test
        @Transactional
        @DisplayName("Deve criar pedido, reservar estoque e aprovar pagamento via gateway simulado")
        void deveCriarPedidoEAprovarPagamento() {
            String emailCliente = "cliente@raizesnordeste.com";
            CriarPedidoRequest pedidoReq = new CriarPedidoRequest(
                    1L,
                    CanalAtendimento.APLICATIVO_MOBILE,
                    "Sem cebola",
                    false,
                    List.of(new ItemPedidoRequest(1L, 2))
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
            assertNotNull(pagResp.nsuAutorizacaoGateway(), "NSU deve ser gerado na aprovação");
        }

        @Test
        @Transactional
        @DisplayName("Pagamento recusado deve cancelar pedido")
        void pagamentoRecusadoDeveCancelarPedido() {
            String emailCliente = "cliente@raizesnordeste.com";
            CriarPedidoRequest pedidoReq = new CriarPedidoRequest(
                    1L,
                    CanalAtendimento.TOTEM_AUTOATENDIMENTO,
                    "Pagamento teste com recusa",
                    false,
                    List.of(new ItemPedidoRequest(2L, 3))
            );

            PedidoResumoResponse pedido = pedidoService.criarPedido(pedidoReq, emailCliente);

            SolicitacaoPagamentoRequest pagReq = new SolicitacaoPagamentoRequest(FormaPagamento.CARTAO_CREDITO, false, "411111111111");
            TransacaoPagamentoResponse pagResp = pedidoService.processarPagamento(pedido.id(), pagReq, emailCliente);

            assertEquals(StatusTransacao.RECUSADA_SALDO_INSUFICIENTE, pagResp.statusTransacao());
            assertNull(pagResp.nsuAutorizacaoGateway(), "NSU não deve existir em recusa");
        }
    }

    // =====================================================================
    // 5. TESTES DE RESTITUIÇÃO DE ESTOQUE
    // =====================================================================

    @Nested
    @DisplayName("5. Restituição Automática de Estoque")
    class EstoqueRestituicaoTests {

        @Test
        @Transactional
        @DisplayName("Deve restituir estoque completamente após recusa de pagamento")
        void deveRestituirEstoqueAposRecusa() {
            String emailCliente = "cliente@raizesnordeste.com";
            Long unidadeId = 1L;
            Long produtoId = 2L; // CUSC-002
            int quantidadePedida = 3;

            // 1. Verificar estoque ANTES da criação do pedido
            int estoqueAntes = estoqueRepository.findByUnidadeIdAndProdutoId(unidadeId, produtoId)
                    .orElseThrow().getQuantidadeDisponivel();

            // 2. Criar pedido (reserva estoque)
            CriarPedidoRequest pedidoReq = new CriarPedidoRequest(
                    unidadeId,
                    CanalAtendimento.BALCAO_HUMANO,
                    "Teste restituição estoque",
                    false,
                    List.of(new ItemPedidoRequest(produtoId, quantidadePedida))
            );
            PedidoResumoResponse pedido = pedidoService.criarPedido(pedidoReq, emailCliente);

            // 3. Verificar débito realizado
            int estoqueAposReserva = estoqueRepository.findByUnidadeIdAndProdutoId(unidadeId, produtoId)
                    .orElseThrow().getQuantidadeDisponivel();
            assertEquals(estoqueAntes - quantidadePedida, estoqueAposReserva,
                    "Estoque deve ter sido debitado na reserva");

            // 4. Recusar pagamento
            SolicitacaoPagamentoRequest pagReq = new SolicitacaoPagamentoRequest(
                    FormaPagamento.CARTAO_CREDITO, false, "411111111111");
            TransacaoPagamentoResponse pagResp = pedidoService.processarPagamento(
                    pedido.id(), pagReq, emailCliente);

            assertEquals(StatusTransacao.RECUSADA_SALDO_INSUFICIENTE, pagResp.statusTransacao());

            // 5. Verificar restituição automática do estoque
            int estoqueFinal = estoqueRepository.findByUnidadeIdAndProdutoId(unidadeId, produtoId)
                    .orElseThrow().getQuantidadeDisponivel();

            assertEquals(estoqueAntes, estoqueFinal,
                    "Estoque final deve ser igual ao estoque inicial (restituição completa)");

            assertThrows(RegraNegocioException.class,
                    () -> pedidoService.processarPagamento(pedido.id(), pagReq, emailCliente),
                    "Uma segunda tentativa não pode restituir o estoque novamente");

            int estoqueAposSegundaTentativa = estoqueRepository
                    .findByUnidadeIdAndProdutoId(unidadeId, produtoId)
                    .orElseThrow().getQuantidadeDisponivel();
            assertEquals(estoqueAntes, estoqueAposSegundaTentativa,
                    "O estoque não pode ser restituído em duplicidade");
        }

        @Test
        @Transactional
        @DisplayName("Pagamento aprovado NÃO deve restituir estoque")
        void pagamentoAprovadoNaoDeveRestituirEstoque() {
            String emailCliente = "cliente@raizesnordeste.com";
            Long unidadeId = 1L;
            Long produtoId = 3L; // TAP-001
            int quantidadePedida = 2;

            int estoqueAntes = estoqueRepository.findByUnidadeIdAndProdutoId(unidadeId, produtoId)
                    .orElseThrow().getQuantidadeDisponivel();

            CriarPedidoRequest pedidoReq = new CriarPedidoRequest(
                    unidadeId,
                    CanalAtendimento.PORTAL_WEB,
                    "Teste sem restituição",
                    false,
                    List.of(new ItemPedidoRequest(produtoId, quantidadePedida))
            );
            PedidoResumoResponse pedido = pedidoService.criarPedido(pedidoReq, emailCliente);

            // Aprovar pagamento
            SolicitacaoPagamentoRequest pagReq = new SolicitacaoPagamentoRequest(
                    FormaPagamento.PIX_ESTANTANEO, true, null);
            pedidoService.processarPagamento(pedido.id(), pagReq, emailCliente);

            int estoqueFinal = estoqueRepository.findByUnidadeIdAndProdutoId(unidadeId, produtoId)
                    .orElseThrow().getQuantidadeDisponivel();

            assertEquals(estoqueAntes - quantidadePedida, estoqueFinal,
                    "Estoque deve permanecer debitado quando pagamento é aprovado");
        }
    }
}
