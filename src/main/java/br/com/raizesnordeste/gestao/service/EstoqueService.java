package br.com.raizesnordeste.gestao.service;

import br.com.raizesnordeste.gestao.domain.model.InventarioEstoque;
import br.com.raizesnordeste.gestao.domain.model.ProdutoCardapio;
import br.com.raizesnordeste.gestao.domain.model.TipoEventoAuditoria;
import br.com.raizesnordeste.gestao.domain.model.UnidadeFranquia;
import br.com.raizesnordeste.gestao.domain.repository.InventarioEstoqueRepository;
import br.com.raizesnordeste.gestao.dto.estoque.InventarioEstoqueRequest;
import br.com.raizesnordeste.gestao.dto.estoque.InventarioEstoqueResponse;
import br.com.raizesnordeste.gestao.exception.EstoqueInsuficienteException;
import br.com.raizesnordeste.gestao.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EstoqueService {

    private final InventarioEstoqueRepository estoqueRepository;
    private final UnidadeService unidadeService;
    private final CardapioService cardapioService;
    private final AuditoriaService auditoriaService;

    public EstoqueService(InventarioEstoqueRepository estoqueRepository,
                          UnidadeService unidadeService,
                          CardapioService cardapioService,
                          AuditoriaService auditoriaService) {
        this.estoqueRepository = estoqueRepository;
        this.unidadeService = unidadeService;
        this.cardapioService = cardapioService;
        this.auditoriaService = auditoriaService;
    }

    @Transactional(readOnly = true)
    public List<InventarioEstoqueResponse> listarPorUnidade(Long unidadeId) {
        return estoqueRepository.findByUnidadeId(unidadeId)
                .stream()
                .map(InventarioEstoqueResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InventarioEstoqueResponse> listarAlertasEstoqueBaixo() {
        return estoqueRepository.buscarEstoquesAbaixoDoMinimo()
                .stream()
                .map(InventarioEstoqueResponse::de)
                .toList();
    }

    @Transactional
    public InventarioEstoqueResponse salvarOuAtualizar(InventarioEstoqueRequest request, String responsavelEmail) {
        UnidadeFranquia unidade = unidadeService.buscarEntidadePorId(request.unidadeId());
        ProdutoCardapio produto = cardapioService.buscarEntidadePorId(request.produtoId());

        InventarioEstoque estoque = estoqueRepository
                .findByUnidadeIdAndProdutoId(unidade.getId(), produto.getId())
                .orElse(new InventarioEstoque(unidade, produto, request.quantidadeDisponivel(), request.quantidadeMinimaSeguranca()));

        estoque.setQuantidadeDisponivel(request.quantidadeDisponivel());
        if (request.quantidadeMinimaSeguranca() != null) {
            estoque.setQuantidadeMinimaSeguranca(request.quantidadeMinimaSeguranca());
        }

        InventarioEstoque salvo = estoqueRepository.save(estoque);

        auditoriaService.registrar(TipoEventoAuditoria.AJUSTE_MANUAL_ESTOQUE, responsavelEmail, "PAINEL_ESTOQUE",
                "InventarioEstoque", String.valueOf(salvo.getId()),
                String.format("Estoque ajustado: %s na unidade %s para saldo %d",
                        produto.getNome(), unidade.getNomeFantasia(), salvo.getQuantidadeDisponivel()));

        return InventarioEstoqueResponse.de(salvo);
    }

    @Transactional
    public void validarEDebitarEstoque(Long unidadeId, Long produtoId, int quantidade) {
        InventarioEstoque estoque = estoqueRepository.findByUnidadeIdAndProdutoId(unidadeId, produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não possui registro de estoque na unidade selecionada."));

        if (!estoque.temEstoqueDisponivel(quantidade)) {
            throw new EstoqueInsuficienteException(estoque.getProduto().getNome(), estoque.getQuantidadeDisponivel(), quantidade);
        }

        estoque.debitarEstoque(quantidade);
        estoqueRepository.save(estoque);
    }

    @Transactional
    public void restituirEstoque(Long unidadeId, Long produtoId, int quantidade) {
        InventarioEstoque estoque = estoqueRepository.findByUnidadeIdAndProdutoId(unidadeId, produtoId)
                .orElse(null);

        if (estoque != null) {
            estoque.restituirEstoque(quantidade);
            estoqueRepository.save(estoque);
        }
    }
}
