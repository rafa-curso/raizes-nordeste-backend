package br.com.raizesnordeste.gestao.service;

import br.com.raizesnordeste.gestao.domain.model.*;
import br.com.raizesnordeste.gestao.domain.repository.PedidoRepository;
import br.com.raizesnordeste.gestao.domain.repository.TransacaoPagamentoRepository;
import br.com.raizesnordeste.gestao.domain.repository.UsuarioRepository;
import br.com.raizesnordeste.gestao.dto.pagamento.SolicitacaoPagamentoRequest;
import br.com.raizesnordeste.gestao.dto.pagamento.TransacaoPagamentoResponse;
import br.com.raizesnordeste.gestao.dto.pedido.*;
import br.com.raizesnordeste.gestao.exception.RecursoNaoEncontradoException;
import br.com.raizesnordeste.gestao.exception.RegraNegocioException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final TransacaoPagamentoRepository transacaoRepository;
    private final AutenticacaoService autenticacaoService;
    private final UsuarioRepository usuarioRepository;
    private final UnidadeService unidadeService;
    private final CardapioService cardapioService;
    private final EstoqueService estoqueService;
    private final GatewayPagamentoExternoService gatewayService;
    private final AuditoriaService auditoriaService;

    public PedidoService(PedidoRepository pedidoRepository,
                         TransacaoPagamentoRepository transacaoRepository,
                         AutenticacaoService autenticacaoService,
                         UsuarioRepository usuarioRepository,
                         UnidadeService unidadeService,
                         CardapioService cardapioService,
                         EstoqueService estoqueService,
                         GatewayPagamentoExternoService gatewayService,
                         AuditoriaService auditoriaService) {
        this.pedidoRepository = pedidoRepository;
        this.transacaoRepository = transacaoRepository;
        this.autenticacaoService = autenticacaoService;
        this.usuarioRepository = usuarioRepository;
        this.unidadeService = unidadeService;
        this.cardapioService = cardapioService;
        this.estoqueService = estoqueService;
        this.gatewayService = gatewayService;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public PedidoResumoResponse criarPedido(CriarPedidoRequest request, String emailUsuario) {
        Usuario cliente = autenticacaoService.buscarEntidadePorEmail(emailUsuario);
        UnidadeFranquia unidade = unidadeService.buscarEntidadePorId(request.unidadeId());

        if (!unidade.isAtivo()) {
            throw new RegraNegocioException("A unidade selecionada está temporariamente inativa.");
        }

        Pedido pedido = new Pedido(cliente, unidade, request.canalOrigem(), request.observacoes());

        // Processar itens e reservar estoque
        for (ItemPedidoRequest itemReq : request.itens()) {
            ProdutoCardapio produto = cardapioService.buscarEntidadePorId(itemReq.produtoId());
            if (!produto.isDisponivel()) {
                throw new RegraNegocioException("O produto '" + produto.getNome() + "' está indisponível para pedidos no momento.");
            }

            // Reserva de estoque com validação
            estoqueService.validarEDebitarEstoque(unidade.getId(), produto.getId(), itemReq.quantidade());

            pedido.adicionarItem(produto, itemReq.quantidade());
        }

        // Resgate opcional de desconto por fidelidade
        if (Boolean.TRUE.equals(request.resgatarDescontoFidelidade()) && cliente.getPontosFidelidade() > 0) {
            // Regra: cada 10 pontos concedem R$ 5,00 de desconto, limitado a 50% do subtotal.
            int pontosDisponiveis = cliente.getPontosFidelidade();
            int blocosDisponiveis = Math.min(pontosDisponiveis, 50) / 10;

            // Como cada benefício é indivisível (10 pontos = R$ 5,00), somente blocos
            // completos que respeitem o teto de 50% podem ser usados.
            BigDecimal valorPorBloco = new BigDecimal("5.00");
            BigDecimal limiteDesconto = pedido.getValorSubtotal().multiply(new BigDecimal("0.50"));
            int blocosPermitidosPeloLimite = limiteDesconto.divideToIntegralValue(valorPorBloco).intValue();
            int blocosEfetivamenteUsados = Math.min(blocosDisponiveis, blocosPermitidosPeloLimite);

            BigDecimal descontoEfetivo = valorPorBloco.multiply(BigDecimal.valueOf(blocosEfetivamenteUsados));
            int pontosEfetivamenteConsumidos = blocosEfetivamenteUsados * 10;

            pedido.setValorDescontoFidelidade(descontoEfetivo);
            cliente.setPontosFidelidade(Math.max(0, pontosDisponiveis - pontosEfetivamenteConsumidos));
            usuarioRepository.save(cliente);
        }

        pedido.recalcularTotais();
        Pedido salvo = pedidoRepository.save(pedido);

        auditoriaService.registrar(TipoEventoAuditoria.PEDIDO_INICIADO, cliente.getEmail(), pedido.getCanalOrigem().name(),
                "Pedido", salvo.getCodigoRastreio(), "Pedido criado no valor total de R$ " + salvo.getValorTotalLiquido());

        auditoriaService.registrar(TipoEventoAuditoria.ESTOQUE_RESERVADO, cliente.getEmail(), pedido.getCanalOrigem().name(),
                "InventarioEstoque", "Unidade " + unidade.getCodigoIdentificador(), "Estoque reservado para os itens do pedido " + salvo.getCodigoRastreio());

        return PedidoResumoResponse.de(salvo);
    }

    @Transactional
    public TransacaoPagamentoResponse processarPagamento(Long pedidoId, SolicitacaoPagamentoRequest request, String emailUsuario) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado com o ID: " + pedidoId));

        Usuario usuarioAutenticado = autenticacaoService.buscarEntidadePorEmail(emailUsuario);
        boolean isAdmin = usuarioAutenticado.getPerfil() != PerfilAcesso.ROLE_CLIENTE;

        if (!isAdmin && !pedido.getCliente().getId().equals(usuarioAutenticado.getId())) {
            throw new AccessDeniedException("Você não possui permissão para pagar este pedido.");
        }

        if (pedido.getStatus() != StatusPedido.RECEBIDO_AGUARDANDO_PAGAMENTO) {
            throw new RegraNegocioException("Não é possível realizar pagamento. Status atual do pedido: " + pedido.getStatus());
        }

        // Chamada ao Gateway Desacoplado
        GatewayPagamentoExternoService.RespostaGateway respostaGateway = gatewayService.processarTransacao(
                request.formaPagamento(),
                pedido.getValorTotalLiquido(),
                request.deveAprovar()
        );

        TransacaoPagamento transacao = new TransacaoPagamento(
                pedido,
                request.formaPagamento(),
                respostaGateway.status(),
                pedido.getValorTotalLiquido(),
                respostaGateway.nsuAutorizacao(),
                respostaGateway.mensagem()
        );

        transacaoRepository.save(transacao);
        pedido.adicionarTransacao(transacao);

        if (respostaGateway.status() == StatusTransacao.APROVADA) {
            pedido.setStatus(StatusPedido.PAGAMENTO_CONFIRMADO);

            // Credita pontos de fidelidade ao cliente
            Usuario cliente = pedido.getCliente();
            cliente.creditarPontos(pedido.getPontosGerados());
            usuarioRepository.save(cliente);

            auditoriaService.registrar(TipoEventoAuditoria.PAGAMENTO_APROVADO, emailUsuario, pedido.getCanalOrigem().name(),
                    "Pedido", pedido.getCodigoRastreio(), "Pagamento aprovado NSU: " + respostaGateway.nsuAutorizacao());
        } else {
            // Tolerância a falhas e restituição automática de estoque
            for (ItemPedido item : pedido.getItens()) {
                estoqueService.restituirEstoque(pedido.getUnidade().getId(), item.getProduto().getId(), item.getQuantidade());
            }

            pedido.setStatus(StatusPedido.CANCELADO_ESTORNADO);

            auditoriaService.registrar(TipoEventoAuditoria.PAGAMENTO_RECUSADO, emailUsuario, pedido.getCanalOrigem().name(),
                    "Pedido", pedido.getCodigoRastreio(), "Pagamento recusado pelo gateway externo. Estoque restituído.");

            auditoriaService.registrar(TipoEventoAuditoria.ESTOQUE_RESTITUIDO, "SISTEMA_COMPENSACAO", pedido.getCanalOrigem().name(),
                    "InventarioEstoque", "Unidade " + pedido.getUnidade().getCodigoIdentificador(),
                    "Estoque estornado automaticamente devido à recusa no pagamento.");
        }

        pedidoRepository.save(pedido);
        return TransacaoPagamentoResponse.de(transacao);
    }

    @Transactional
    public PedidoResumoResponse atualizarStatus(Long pedidoId, AtualizarStatusPedidoRequest request, String emailUsuario) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado com o ID: " + pedidoId));

        StatusPedido statusAnterior = pedido.getStatus();
        StatusPedido novoStatus = request.novoStatus();

        if (statusAnterior == StatusPedido.CANCELADO_ESTORNADO) {
            throw new RegraNegocioException("Pedidos cancelados não podem ter seu status alterado.");
        }

        if (novoStatus == StatusPedido.CANCELADO_ESTORNADO && statusAnterior != StatusPedido.CONCLUIDO_ENTREGUE) {
            // Se cancelado pela operação, restitui o estoque
            for (ItemPedido item : pedido.getItens()) {
                estoqueService.restituirEstoque(pedido.getUnidade().getId(), item.getProduto().getId(), item.getQuantidade());
            }
            auditoriaService.registrar(TipoEventoAuditoria.ESTOQUE_RESTITUIDO, emailUsuario, "PAINEL_GESTAO",
                    "Pedido", pedido.getCodigoRastreio(), "Estoque restituído devido ao cancelamento do pedido.");
        }

        pedido.setStatus(novoStatus);
        Pedido salvo = pedidoRepository.save(pedido);

        auditoriaService.registrar(TipoEventoAuditoria.STATUS_PEDIDO_ALTERADO, emailUsuario, "PAINEL_GESTAO",
                "Pedido", salvo.getCodigoRastreio(),
                String.format("Status alterado de %s para %s. Justificativa: %s",
                        statusAnterior, novoStatus, request.justificativaOperacional() != null ? request.justificativaOperacional() : "Operação de rotina"));

        return PedidoResumoResponse.de(salvo);
    }

    @Transactional(readOnly = true)
    public PedidoDetalhadoResponse buscarPorId(Long pedidoId, String emailUsuario, boolean isAdmin) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado com o ID: " + pedidoId));

        Usuario solicitante = autenticacaoService.buscarEntidadePorEmail(emailUsuario);

        if (!isAdmin && !pedido.getCliente().getId().equals(solicitante.getId())) {
            throw new AccessDeniedException("Você não possui permissão para consultar os detalhes deste pedido.");
        }

        return PedidoDetalhadoResponse.de(pedido);
    }

    @Transactional(readOnly = true)
    public List<PedidoResumoResponse> listarPedidos(CanalAtendimento canal, StatusPedido status, Long unidadeId,
                                                   String emailUsuario, boolean isAdmin) {
        if (!isAdmin) {
            Usuario cliente = autenticacaoService.buscarEntidadePorEmail(emailUsuario);
            return pedidoRepository.findByClienteIdOrderByDataHoraAberturaDesc(cliente.getId())
                    .stream()
                    .map(PedidoResumoResponse::de)
                    .toList();
        }

        return pedidoRepository.filtrarPedidos(canal, status, unidadeId)
                .stream()
                .map(PedidoResumoResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TransacaoPagamentoResponse> listarTransacoes(Long pedidoId, String emailUsuario, boolean isAdmin) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado com o ID: " + pedidoId));

        Usuario solicitante = autenticacaoService.buscarEntidadePorEmail(emailUsuario);

        if (!isAdmin && !pedido.getCliente().getId().equals(solicitante.getId())) {
            throw new AccessDeniedException("Você não possui permissão para visualizar as transações deste pedido.");
        }

        return transacaoRepository.findByPedidoIdOrderByDataHoraProcessamentoDesc(pedidoId)
                .stream()
                .map(TransacaoPagamentoResponse::de)
                .toList();
    }
}
