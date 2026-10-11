package br.labprog.fluxarte.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.NoSuchElementException;

@ControllerAdvice
public class ExceptionGlobalHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroPadrao> recursoNaoEncontrado(RecursoNaoEncontradoException excecao,
            HttpServletRequest requisicao) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErroPadrao erro = new ErroPadrao();
        erro.setMomento(Instant.now());
        erro.setStatus(status.value());
        erro.setErro("Recurso não encontrado");
        erro.setMensagem(excecao.getMessage() != null && !excecao.getMessage().isBlank()
                ? excecao.getMessage()
                : "O recurso solicitado não foi encontrado");
        erro.setCaminho(requisicao.getRequestURI());
        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErroPadrao> noSuchElement(NoSuchElementException excecao,
            HttpServletRequest requisicao) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErroPadrao erro = new ErroPadrao();
        erro.setMomento(Instant.now());
        erro.setStatus(status.value());
        erro.setErro("Recurso não encontrado");
        erro.setMensagem(excecao.getMessage() != null && !excecao.getMessage().isBlank()
                ? excecao.getMessage()
                : "O recurso solicitado não foi encontrado");
        erro.setCaminho(requisicao.getRequestURI());
        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroPadrao> accessDenied(AccessDeniedException excecao,
            HttpServletRequest requisicao) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        ErroPadrao erro = new ErroPadrao();
        erro.setMomento(Instant.now());
        erro.setStatus(status.value());
        erro.setErro("Acesso Negado");
        erro.setMensagem("Você não tem permissão para acessar este recurso");
        erro.setCaminho(requisicao.getRequestURI());
        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<ErroPadrao> acessoNegado(AcessoNegadoException excecao,
            HttpServletRequest requisicao) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        ErroPadrao erro = new ErroPadrao();
        erro.setMomento(Instant.now());
        erro.setStatus(status.value());
        erro.setErro("Acesso Negado");
        erro.setMensagem(excecao.getMessage() != null && !excecao.getMessage().isBlank()
                ? excecao.getMessage()
                : "Você não tem permissão para acessar este recurso");
        erro.setCaminho(requisicao.getRequestURI());
        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroPadrao> regraNegocio(RegraNegocioException excecao,
            HttpServletRequest requisicao) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErroPadrao erro = new ErroPadrao();
        erro.setMomento(Instant.now());
        erro.setStatus(status.value());
        erro.setErro("Operação não permitida");
        erro.setMensagem(excecao.getMessage());
        erro.setCaminho(requisicao.getRequestURI());
        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErroPadrao> conflito(ConflitoException excecao,
            HttpServletRequest requisicao) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErroPadrao erro = new ErroPadrao();
        erro.setMomento(Instant.now());
        erro.setStatus(status.value());
        erro.setErro("Conflito de dados");
        erro.setMensagem(excecao.getMessage());
        erro.setCaminho(requisicao.getRequestURI());
        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroPadrao> integridadeDados(DataIntegrityViolationException excecao,
            HttpServletRequest requisicao) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErroPadrao erro = new ErroPadrao();
        erro.setMomento(Instant.now());
        erro.setStatus(status.value());
        erro.setErro("Conflito de dados");
        erro.setMensagem("Não foi possível concluir a operação devido a um conflito com dados existentes");
        erro.setCaminho(requisicao.getRequestURI());
        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroValidacao> validacao(MethodArgumentNotValidException excecao,
            HttpServletRequest requisicao) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErroValidacao erro = new ErroValidacao();
        erro.setMomento(Instant.now());
        erro.setStatus(status.value());
        erro.setErro("Dados inválidos");
        erro.setMensagem("Verifique os campos informados");
        erro.setCaminho(requisicao.getRequestURI());

        for (FieldError campo : excecao.getBindingResult().getFieldErrors()) {
            erro.adicionarErro(campo.getField(), campo.getDefaultMessage());
        }

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroPadrao> corpoInvalido(HttpMessageNotReadableException excecao,
            HttpServletRequest requisicao) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErroPadrao erro = new ErroPadrao();
        erro.setMomento(Instant.now());
        erro.setStatus(status.value());
        erro.setErro("Requisição inválida");
        erro.setMensagem("Formato de requisição incorreto ou dados ilegíveis");
        erro.setCaminho(requisicao.getRequestURI());
        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroPadrao> parametroTipoInvalido(MethodArgumentTypeMismatchException excecao,
            HttpServletRequest requisicao) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErroPadrao erro = new ErroPadrao();
        erro.setMomento(Instant.now());
        erro.setStatus(status.value());
        erro.setErro("Parâmetro inválido");
        erro.setMensagem("Um ou mais parâmetros informados possuem tipo ou formato incompatível");
        erro.setCaminho(requisicao.getRequestURI());
        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErroPadrao> falhaAutenticacao(AuthenticationException excecao,
            HttpServletRequest requisicao) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErroPadrao erro = new ErroPadrao();
        erro.setMomento(Instant.now());
        erro.setStatus(status.value());
        erro.setErro("Falha na Autenticação");
        erro.setMensagem(excecao instanceof BadCredentialsException
                ? "E-mail ou senha incorretos"
                : "Não foi possível autenticar. Verifique suas credenciais");
        erro.setCaminho(requisicao.getRequestURI());
        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroPadrao> argumentoInvalido(IllegalArgumentException excecao,
            HttpServletRequest requisicao) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErroPadrao erro = new ErroPadrao();
        erro.setMomento(Instant.now());
        erro.setStatus(status.value());
        erro.setErro("Requisição Inválida");
        erro.setMensagem(excecao.getMessage() != null && !excecao.getMessage().isBlank()
                ? excecao.getMessage()
                : "Parâmetros informados são inválidos");
        erro.setCaminho(requisicao.getRequestURI());
        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroPadrao> erroGenerico(Exception excecao,
            HttpServletRequest requisicao) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErroPadrao erro = new ErroPadrao();
        erro.setMomento(Instant.now());
        erro.setStatus(status.value());
        erro.setErro("Erro Interno do Servidor");
        erro.setMensagem("Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.");
        erro.setCaminho(requisicao.getRequestURI());
        return ResponseEntity.status(status).body(erro);
    }
}