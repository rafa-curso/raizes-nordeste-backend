package br.com.raizesnordeste.gestao.domain.repository;

import br.com.raizesnordeste.gestao.domain.model.UnidadeFranquia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnidadeFranquiaRepository extends JpaRepository<UnidadeFranquia, Long> {
    Optional<UnidadeFranquia> findByCodigoIdentificador(String codigoIdentificador);
    boolean existsByCodigoIdentificador(String codigoIdentificador);
    List<UnidadeFranquia> findByAtivoTrue();
}
