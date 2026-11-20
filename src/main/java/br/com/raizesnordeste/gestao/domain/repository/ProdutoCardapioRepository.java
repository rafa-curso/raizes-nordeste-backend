package br.com.raizesnordeste.gestao.domain.repository;

import br.com.raizesnordeste.gestao.domain.model.CategoriaCardapio;
import br.com.raizesnordeste.gestao.domain.model.ProdutoCardapio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoCardapioRepository extends JpaRepository<ProdutoCardapio, Long> {
    Optional<ProdutoCardapio> findByCodigoSku(String codigoSku);
    boolean existsByCodigoSku(String codigoSku);
    List<ProdutoCardapio> findByDisponivelTrue();
    List<ProdutoCardapio> findByCategoriaAndDisponivelTrue(CategoriaCardapio categoria);
}
