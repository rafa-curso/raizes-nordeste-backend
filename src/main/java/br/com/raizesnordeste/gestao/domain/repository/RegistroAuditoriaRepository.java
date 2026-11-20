package br.com.raizesnordeste.gestao.domain.repository;

import br.com.raizesnordeste.gestao.domain.model.RegistroAuditoria;
import br.com.raizesnordeste.gestao.domain.model.TipoEventoAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroAuditoriaRepository extends JpaRepository<RegistroAuditoria, Long> {
    List<RegistroAuditoria> findByUsuarioResponsavelOrderByDataHoraRegistroDesc(String usuarioResponsavel);
    List<RegistroAuditoria> findByTipoEventoOrderByDataHoraRegistroDesc(TipoEventoAuditoria tipoEvento);
    List<RegistroAuditoria> findTop100ByOrderByDataHoraRegistroDesc();
}
