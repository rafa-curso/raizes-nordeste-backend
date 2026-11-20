package br.com.raizesnordeste.gestao.web.controller;

import br.com.raizesnordeste.gestao.domain.model.RegistroAuditoria;
import br.com.raizesnordeste.gestao.service.AuditoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auditoria")
@Tag(name = "6. Trilha de Auditoria e Governança", description = "Rastreabilidade de operações críticas, conformidade LGPD e segurança")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN_MATRIZ')")
    @Operation(summary = "Lista os últimos registros de auditoria do sistema (Apenas Administrador Matriz)")
    public ResponseEntity<List<RegistroAuditoria>> listarAuditoria() {
        return ResponseEntity.ok(auditoriaService.listarUltimosRegistros());
    }
}
