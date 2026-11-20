package br.com.raizesnordeste.gestao.domain.repository;

import br.com.raizesnordeste.gestao.domain.model.InventarioEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioEstoqueRepository extends JpaRepository<InventarioEstoque, Long> {
    Optional<InventarioEstoque> findByUnidadeIdAndProdutoId(Long unidadeId, Long produtoId);
    List<InventarioEstoque> findByUnidadeId(Long unidadeId);

    @Query("SELECT e FROM InventarioEstoque e WHERE e.quantidadeDisponivel <= e.quantidadeMinimaSeguranca")
    List<InventarioEstoque> buscarEstoquesAbaixoDoMinimo();
}
