package br.com.raizesnordeste.gestao.service;

import br.com.raizesnordeste.gestao.domain.model.TipoEventoAuditoria;
import br.com.raizesnordeste.gestao.domain.model.UnidadeFranquia;
import br.com.raizesnordeste.gestao.domain.repository.UnidadeFranquiaRepository;
import br.com.raizesnordeste.gestao.dto.unidade.UnidadeRequest;
import br.com.raizesnordeste.gestao.dto.unidade.UnidadeResponse;
import br.com.raizesnordeste.gestao.exception.ConflitoRegistroException;
import br.com.raizesnordeste.gestao.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UnidadeService {

    private final UnidadeFranquiaRepository unidadeRepository;
    private final AuditoriaService auditoriaService;

    public UnidadeService(UnidadeFranquiaRepository unidadeRepository, AuditoriaService auditoriaService) {
        this.unidadeRepository = unidadeRepository;
        this.auditoriaService = auditoriaService;
    }

    @Transactional(readOnly = true)
    public List<UnidadeResponse> listarAtivas() {
        return unidadeRepository.findByAtivoTrue()
                .stream()
                .map(UnidadeResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public UnidadeResponse buscarPorId(Long id) {
        return UnidadeResponse.de(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public UnidadeFranquia buscarEntidadePorId(Long id) {
        return unidadeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade da franquia não encontrada com o ID: " + id));
    }

    @Transactional
    public UnidadeResponse cadastrar(UnidadeRequest request, String responsavelEmail) {
        if (unidadeRepository.existsByCodigoIdentificador(request.codigoIdentificador().trim().toUpperCase())) {
            throw new ConflitoRegistroException("Já existe uma unidade com o código identificador: " + request.codigoIdentificador());
        }

        UnidadeFranquia unidade = new UnidadeFranquia(
                request.codigoIdentificador().trim().toUpperCase(),
                request.nomeFantasia().trim(),
                request.cidade().trim(),
                request.uf().trim().toUpperCase(),
                request.enderecoCompleto().trim(),
                request.telefoneContato(),
                request.formatoOperacao()
        );

        UnidadeFranquia salva = unidadeRepository.save(unidade);

        auditoriaService.registrar(TipoEventoAuditoria.AJUSTE_MANUAL_ESTOQUE, responsavelEmail, "PAINEL_GESTAO",
                "UnidadeFranquia", salva.getCodigoIdentificador(), "Nova unidade cadastrada na rede: " + salva.getNomeFantasia());

        return UnidadeResponse.de(salva);
    }
}
