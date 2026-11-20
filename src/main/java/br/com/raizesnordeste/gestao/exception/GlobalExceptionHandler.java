package br.com.raizesnordeste.gestao.exception;

import br.com.raizesnordeste.gestao.dto.comum.ProblemaDetalhes;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ProblemaDetalhes> tratarNaoEncontrado(RecursoNaoEncontradoException ex, HttpServletRequest req) {
        ProblemaDetalhes prob = ProblemaDetalhes.construir(
            "urn:problema:recurso-nao-encontrado",
            "Recurso Solicitado Não Encontrado",
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            req.getRequestURI(),
            null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(prob);
    }

    @ExceptionHandler(ConflitoRegistroException.class)
    public ResponseEntity<ProblemaDetalhes> tratarConflito(ConflitoRegistroException ex, HttpServletRequest req) {
        ProblemaDetalhes prob = ProblemaDetalhes.construir(
            "urn:problema:conflito-dados",
            "Conflito de Informações Cadastrais",
            HttpStatus.CONFLICT.value(),
            ex.getMessage(),
            req.getRequestURI(),
            null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(prob);
    }

    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ResponseEntity<ProblemaDetalhes> tratarEstoqueInsuficiente(EstoqueInsuficienteException ex, HttpServletRequest req) {
        ProblemaDetalhes prob = ProblemaDetalhes.construir(
            "urn:problema:estoque-insuficiente",
            "Saldo de Estoque Insuficiente",
            HttpStatus.CONFLICT.value(),
            ex.getMessage(),
            req.getRequestURI(),
            null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(prob);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ProblemaDetalhes> tratarRegraNegocio(RegraNegocioException ex, HttpServletRequest req) {
        ProblemaDetalhes prob = ProblemaDetalhes.construir(
            "urn:problema:regra-negocio",
            "Violação de Regra de Negócio",
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            req.getRequestURI(),
            null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(prob);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemaDetalhes> tratarValidacao(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ProblemaDetalhes.CampoInvalido> campos = new ArrayList<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            campos.add(new ProblemaDetalhes.CampoInvalido(fe.getField(), fe.getDefaultMessage()));
        }

        ProblemaDetalhes prob = ProblemaDetalhes.construir(
            "urn:problema:dados-invalidos",
            "Falha de Validação nos Parâmetros de Entrada",
            HttpStatus.BAD_REQUEST.value(),
            "Um ou mais campos da requisição possuem valores inválidos ou ausentes.",
            req.getRequestURI(),
            campos
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(prob);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemaDetalhes> tratarCredenciaisInvalidas(BadCredentialsException ex, HttpServletRequest req) {
        ProblemaDetalhes prob = ProblemaDetalhes.construir(
            "urn:problema:credenciais-invalidas",
            "Falha na Autenticação",
            HttpStatus.UNAUTHORIZED.value(),
            "E-mail ou senha informados estão incorretos.",
            req.getRequestURI(),
            null
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(prob);
    }

    @ExceptionHandler(AcessoNaoAutorizadoException.class)
    public ResponseEntity<ProblemaDetalhes> tratarNaoAutorizado(AcessoNaoAutorizadoException ex, HttpServletRequest req) {
        ProblemaDetalhes prob = ProblemaDetalhes.construir(
            "urn:problema:nao-autorizado",
            "Acesso Não Autorizado",
            HttpStatus.UNAUTHORIZED.value(),
            ex.getMessage(),
            req.getRequestURI(),
            null
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(prob);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemaDetalhes> tratarAcessoNegado(AccessDeniedException ex, HttpServletRequest req) {
        ProblemaDetalhes prob = ProblemaDetalhes.construir(
            "urn:problema:acesso-proibido",
            "Permissão Insuficiente",
            HttpStatus.FORBIDDEN.value(),
            "O perfil atual não possui permissão para executar esta operação.",
            req.getRequestURI(),
            null
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(prob);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemaDetalhes> tratarGeral(Exception ex, HttpServletRequest req) {
        ProblemaDetalhes prob = ProblemaDetalhes.construir(
            "urn:problema:erro-interno",
            "Erro Interno no Servidor",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Ocorreu um erro interno inesperado durante o processamento da requisição: " + ex.getMessage(),
            req.getRequestURI(),
            null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(prob);
    }
}
