package br.com.raizesnordeste.gestao.domain.repository;

import br.com.raizesnordeste.gestao.domain.model.CanalAtendimento;
import br.com.raizesnordeste.gestao.domain.model.Pedido;
import br.com.raizesnordeste.gestao.domain.model.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Optional<Pedido> findByCodigoRastreio(String codigoRastreio);
    List<Pedido> findByClienteIdOrderByDataHoraAberturaDesc(Long clienteId);
    List<Pedido> findByUnidadeIdOrderByDataHoraAberturaDesc(Long unidadeId);

    @Query("SELECT p FROM Pedido p WHERE " +
           "(:canal IS NULL OR p.canalOrigem = :canal) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:unidadeId IS NULL OR p.unidade.id = :unidadeId) " +
           "ORDER BY p.dataHoraAbertura DESC")
    List<Pedido> filtrarPedidos(@Param("canal") CanalAtendimento canal,
                                @Param("status") StatusPedido status,
                                @Param("unidadeId") Long unidadeId);
}
