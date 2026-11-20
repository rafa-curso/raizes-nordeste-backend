package br.com.raizesnordeste.gestao.service;

import br.com.raizesnordeste.gestao.domain.model.CategoriaCardapio;
import br.com.raizesnordeste.gestao.domain.model.ProdutoCardapio;
import br.com.raizesnordeste.gestao.domain.model.TipoEventoAuditoria;
import br.com.raizesnordeste.gestao.domain.repository.ProdutoCardapioRepository;
import br.com.raizesnordeste.gestao.dto.cardapio.ProdutoCardapioRequest;
import br.com.raizesnordeste.gestao.dto.cardapio.ProdutoCardapioResponse;
import br.com.raizesnordeste.gestao.exception.ConflitoRegistroException;
import br.com.raizesnordeste.gestao.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CardapioService {

    private final ProdutoCardapioRepository produtoRepository;
    private final AuditoriaService auditoriaService;

    public CardapioService(ProdutoCardapioRepository produtoRepository, AuditoriaService auditoriaService) {
        this.produtoRepository = produtoRepository;
        this.auditoriaService = auditoriaService;
    }

    @Transactional(readOnly = true)
    public List<ProdutoCardapioResponse> listarDisponiveis(CategoriaCardapio categoria) {
        List<ProdutoCardapio> lista = (categoria != null)
                ? produtoRepository.findByCategoriaAndDisponivelTrue(categoria)
                : produtoRepository.findByDisponivelTrue();

        return lista.stream().map(ProdutoCardapioResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ProdutoCardapioResponse buscarPorId(Long id) {
        return ProdutoCardapioResponse.de(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public ProdutoCardapio buscarEntidadePorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto do cardápio não encontrado com o ID: " + id));
    }

    @Transactional
    public ProdutoCardapioResponse cadastrar(ProdutoCardapioRequest request, String responsavelEmail) {
        String sku = request.codigoSku().trim().toUpperCase();
        if (produtoRepository.existsByCodigoSku(sku)) {
            throw new ConflitoRegistroException("Já existe um produto com o SKU: " + sku);
        }

        ProdutoCardapio produto = new ProdutoCardapio(
                sku,
                request.nome().trim(),
                request.descricao(),
                request.precoVenda(),
                request.categoria(),
                Boolean.TRUE.equals(request.sazonalJunino())
        );

        if (request.disponivel() != null) {
            produto.setDisponivel(request.disponivel());
        }

        ProdutoCardapio salvo = produtoRepository.save(produto);

        auditoriaService.registrar(TipoEventoAuditoria.CARDAPIO_ATUALIZADO, responsavelEmail, "PAINEL_CARDAPIO",
                "ProdutoCardapio", salvo.getCodigoSku(), "Item adicionado ao cardápio: " + salvo.getNome());

        return ProdutoCardapioResponse.de(salvo);
    }
}
