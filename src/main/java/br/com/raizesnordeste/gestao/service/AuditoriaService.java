package br.com.raizesnordeste.gestao.service;

import br.com.raizesnordeste.gestao.domain.model.RegistroAuditoria;
import br.com.raizesnordeste.gestao.domain.model.TipoEventoAuditoria;
import br.com.raizesnordeste.gestao.domain.repository.RegistroAuditoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditoriaService {

    private final RegistroAuditoriaRepository auditoriaRepository;

    public AuditoriaService(RegistroAuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public void registrar(TipoEventoAuditoria tipo, String responsavel, String canal,
                          String entidade, String id, String detalhes) {
        RegistroAuditoria log = new RegistroAuditoria(
            tipo,
            responsavel != null ? responsavel : "SISTEMA",
            canal != null ? canal : "API_REST",
            entidade,
            id,
            detalhes
        );
        auditoriaRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<RegistroAuditoria> listarUltimosRegistros() {
        return auditoriaRepository.findTop100ByOrderByDataHoraRegistroDesc();
    }
}
